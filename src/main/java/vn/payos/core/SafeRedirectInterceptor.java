package vn.payos.core;

import java.io.IOException;
import java.net.ProtocolException;
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
 * (OkHttp's own redirect handling is disabled): at most {@value #MAX_REDIRECTS} hops, credential
 * headers are removed when a hop changes origin (scheme, host or port), and a 303 response, or a
 * 301/302 response to a POST, switches to GET without body or {@code Content-Type}.
 */
final class SafeRedirectInterceptor implements Interceptor {
  static final int MAX_REDIRECTS = 5;

  private static final String[] SENSITIVE = {
    "x-api-key", "x-client-id", "x-partner-code", "authorization", "cookie", "proxy-authorization"
  };

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
    return client
        .newBuilder()
        .followRedirects(false)
        .followSslRedirects(false)
        .addInterceptor(new SafeRedirectInterceptor(client.followSslRedirects()))
        .build();
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    for (int hop = 0; ; hop++) {
      Response response = chain.proceed(request);
      int status = response.code();
      String location = status >= 300 && status < 400 ? response.header("Location") : null;
      HttpUrl next = location != null ? request.url().resolve(location) : null;
      if (next == null || (!followSslRedirects && !next.scheme().equals(request.url().scheme()))) {
        return response;
      }
      response.close();
      if (hop >= MAX_REDIRECTS) {
        throw new ProtocolException("Too many redirects (more than " + MAX_REDIRECTS + ")");
      }

      Request.Builder builder = request.newBuilder().url(next);
      if (!sameOrigin(request.url(), next)) {
        for (String name : SENSITIVE) {
          builder.removeHeader(name);
        }
      }
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

  private static boolean sameOrigin(HttpUrl a, HttpUrl b) {
    return a.scheme().equals(b.scheme()) && a.host().equals(b.host()) && a.port() == b.port();
  }
}
