package vn.payos.core;

import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Network interceptor that removes payOS credential headers when a redirect leaves the origin of
 * the original call. OkHttp only drops {@code Authorization} on cross-host redirects, so custom
 * headers such as {@code x-api-key} would otherwise be forwarded to the new host.
 */
final class SafeRedirectInterceptor implements Interceptor {
  private static final String[] SENSITIVE = {"x-api-key", "x-client-id", "x-partner-code"};

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    HttpUrl original = chain.call().request().url();
    HttpUrl current = request.url();
    if (!sameOrigin(original, current)) {
      Request.Builder builder = request.newBuilder();
      for (String name : SENSITIVE) {
        builder.removeHeader(name);
      }
      request = builder.build();
    }
    return chain.proceed(request);
  }

  private static boolean sameOrigin(HttpUrl a, HttpUrl b) {
    return a.scheme().equals(b.scheme()) && a.host().equals(b.host()) && a.port() == b.port();
  }
}
