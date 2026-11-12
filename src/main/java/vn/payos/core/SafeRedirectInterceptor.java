package vn.payos.core;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.Collections;
import java.util.List;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Application interceptor that follows redirects itself so credentials never leave the API origin.
 *
 * <p>OkHttp only drops {@code Authorization} on cross-host redirects, so custom headers such as
 * {@code x-api-key} would otherwise be forwarded to the new host. Redirects are followed manually
 * (OkHttp's own redirect handling is disabled): only 301, 302, 303, 307 and 308 responses with a
 * {@code Location} header are followed, at most {@value #MAX_REDIRECTS} hops. Once a hop leaves the
 * original origin (scheme, host or port), credential headers and cookie-jar cookies are no longer
 * sent for the rest of the chain, and a 303 response, or a 301/302 response to a POST, switches to
 * GET without body or {@code Content-Type}.
 */
final class SafeRedirectInterceptor implements Interceptor {
  static final int MAX_REDIRECTS = 5;

  private static final String[] SENSITIVE = {
    "x-api-key", "x-client-id", "x-partner-code", "authorization", "cookie", "proxy-authorization"
  };

  // CookieJar.loadForRequest only sees the URL; BridgeInterceptor calls it on this thread while the
  // hop is in flight, so the flag tells the wrapped jar that the chain has left the origin.
  private static final ThreadLocal<Boolean> LEFT_ORIGIN = new ThreadLocal<>();

  /** Raised when a redirect chain is longer than {@code MAX_REDIRECTS} hops; never retried. */
  static final class TooManyRedirectsException extends ProtocolException {
    private static final long serialVersionUID = 1L;

    TooManyRedirectsException() {
      super("Too many redirects (more than " + MAX_REDIRECTS + ")");
    }
  }

  private final boolean followSslRedirects;

  private SafeRedirectInterceptor(boolean followSslRedirects) {
    this.followSslRedirects = followSslRedirects;
  }

  /**
   * Return a client that follows redirects through this interceptor. A client configured with
   * {@code followRedirects(false)} is returned unchanged, so its redirects are never followed; its
   * {@code followSslRedirects} setting is kept.
   *
   * @param client the configured client
   * @return a client sharing the same pools with redirect handling replaced
   */
  static OkHttpClient install(OkHttpClient client) {
    if (!client.followRedirects()) {
      return client;
    }
    OkHttpClient.Builder builder =
        client
            .newBuilder()
            .followRedirects(false)
            .followSslRedirects(false)
            .addInterceptor(new SafeRedirectInterceptor(client.followSslRedirects()));
    if (client.cookieJar() != CookieJar.NO_COOKIES) {
      builder.cookieJar(new OriginCookieJar(client.cookieJar()));
    }
    return builder.build();
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    HttpUrl original = request.url();
    boolean leftOrigin = false;
    for (int hop = 0; ; hop++) {
      Response response = proceed(chain, request, leftOrigin);
      HttpUrl next = redirectTarget(request, response);
      if (next == null) {
        return response;
      }
      response.close();
      if (hop >= MAX_REDIRECTS) {
        throw new TooManyRedirectsException();
      }

      Request.Builder builder = request.newBuilder().url(next);
      leftOrigin = leftOrigin || !sameOrigin(original, next);
      if (leftOrigin) {
        for (String name : SENSITIVE) {
          builder.removeHeader(name);
        }
      }
      int status = response.code();
      boolean switchToGet =
          status == 303 || ((status == 301 || status == 302) && "POST".equals(request.method()));
      if (switchToGet) {
        builder
            .method("GET", null)
            .removeHeader("Content-Type")
            .removeHeader("Content-Length")
            .removeHeader("Transfer-Encoding");
      }
      request = builder.build();
    }
  }

  private static Response proceed(Chain chain, Request request, boolean leftOrigin)
      throws IOException {
    // Save and restore so a nested SDK call on this thread (e.g. from an Authenticator) keeps its
    // own state.
    Boolean previous = LEFT_ORIGIN.get();
    LEFT_ORIGIN.set(leftOrigin);
    try {
      return chain.proceed(request);
    } finally {
      if (previous == null) {
        LEFT_ORIGIN.remove();
      } else {
        LEFT_ORIGIN.set(previous);
      }
    }
  }

  private HttpUrl redirectTarget(Request request, Response response) {
    switch (response.code()) {
      case 301:
      case 302:
      case 303:
      case 307:
      case 308:
        break;
      default:
        return null;
    }
    String location = response.header("Location");
    HttpUrl next = location != null ? request.url().resolve(location) : null;
    if (next == null || (!followSslRedirects && !next.scheme().equals(request.url().scheme()))) {
      return null;
    }
    return next;
  }

  private static boolean sameOrigin(HttpUrl a, HttpUrl b) {
    return a.scheme().equals(b.scheme()) && a.host().equals(b.host()) && a.port() == b.port();
  }

  /** Cookie jar that sends no cookies on hops after the chain has left the original origin. */
  private static final class OriginCookieJar implements CookieJar {
    private final CookieJar delegate;

    OriginCookieJar(CookieJar delegate) {
      this.delegate = delegate;
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
      return Boolean.TRUE.equals(LEFT_ORIGIN.get())
          ? Collections.emptyList()
          : delegate.loadForRequest(url);
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
      delegate.saveFromResponse(url, cookies);
    }
  }
}
