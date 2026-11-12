package vn.payos.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import vn.payos.PayOS;
import vn.payos.PayOSAsync;
import vn.payos.exception.APIException;
import vn.payos.exception.ConnectionException;
import vn.payos.exception.PayOSException;

/** Redirects followed by the SDK keep credential headers on the API origin only. */
public class RedirectHeaderHandlingTest {
  private static final String[] CREDENTIAL_HEADERS = {
    "x-api-key", "x-client-id", "x-partner-code", "authorization", "cookie", "proxy-authorization"
  };
  private static final String NOT_FOUND = "{\"code\":\"101\",\"desc\":\"not found\"}";

  private LocalHttpServer origin;
  private LocalHttpServer other;

  @AfterEach
  void tearDown() {
    if (origin != null) origin.close();
    if (other != null) other.close();
  }

  private static LocalHttpServer.Reply notFound() {
    return LocalHttpServer.Reply.json(404, NOT_FOUND);
  }

  private static PayOS client(String baseUrl, OkHttpClient httpClient) {
    return new PayOS(
        ClientOptions.builder()
            .clientId("client-id")
            .apiKey("api-key")
            .checksumKey("checksum-key")
            .partnerCode("partner-code")
            .baseURL(baseUrl)
            .maxRetries(0)
            .httpClient(httpClient)
            .build());
  }

  /** Options with the SDK's default retry settings. */
  private static ClientOptions defaultRetryOptions(String baseUrl) {
    return ClientOptions.builder()
        .clientId("client-id")
        .apiKey("api-key")
        .checksumKey("checksum-key")
        .baseURL(baseUrl)
        .build();
  }

  private static Throwable callAsync(Supplier<CompletableFuture<?>> future) {
    try {
      future.get().join();
      return null;
    } catch (CompletionException e) {
      return e.getCause();
    }
  }

  /** Default client plus caller-supplied cookie/authorization headers on every request. */
  private static PayOS clientWithExtraCredentials(String baseUrl) {
    OkHttpClient http =
        new OkHttpClient.Builder()
            .addInterceptor(
                chain ->
                    chain.proceed(
                        chain
                            .request()
                            .newBuilder()
                            .header("authorization", "Bearer token")
                            .header("cookie", "session=1")
                            .header("proxy-authorization", "Basic abc")
                            .build()))
            .build();
    return client(baseUrl, http);
  }

  private static Throwable call(Runnable r) {
    try {
      r.run();
      return null;
    } catch (RuntimeException e) {
      return e;
    }
  }

  private static void assertCredentialsPresent(LocalHttpServer.Recorded rec, boolean extra) {
    assertEquals("api-key", rec.header("x-api-key"));
    assertEquals("client-id", rec.header("x-client-id"));
    assertEquals("partner-code", rec.header("x-partner-code"));
    if (extra) {
      assertEquals("Bearer token", rec.header("authorization"));
      assertEquals("session=1", rec.header("cookie"));
      assertEquals("Basic abc", rec.header("proxy-authorization"));
    }
  }

  private static void assertCredentialsAbsent(LocalHttpServer.Recorded rec) {
    for (String h : CREDENTIAL_HEADERS) {
      assertNull(rec.header(h), h + " must not be sent to " + rec.rawPath);
    }
  }

  @Test
  void crossOriginRedirectRemovesCredentialHeaders() throws Exception {
    other = new LocalHttpServer(rec -> notFound());
    origin =
        new LocalHttpServer(rec -> LocalHttpServer.Reply.redirect(302, other.url() + "/landing"));
    call(() -> clientWithExtraCredentials(origin.url()).paymentRequests().get("abc"));

    assertCredentialsPresent(origin.requests().get(0), true);
    List<LocalHttpServer.Recorded> hops = other.requests();
    assertEquals(1, hops.size());
    assertEquals("/landing", hops.get(0).rawPath);
    assertCredentialsAbsent(hops.get(0));
  }

  @Test
  void differentPortOnSameHostCountsAsCrossOrigin() throws Exception {
    other = new LocalHttpServer(rec -> notFound());
    origin =
        new LocalHttpServer(
            rec -> LocalHttpServer.Reply.redirect(307, "http://127.0.0.1:" + other.port() + "/p"));
    call(() -> client(origin.url(), null).paymentRequests().get("abc"));

    assertEquals(1, other.requests().size());
    assertCredentialsAbsent(other.requests().get(0));
  }

  @Test
  void differentHostOnSamePortCountsAsCrossOrigin() throws Exception {
    AtomicReference<LocalHttpServer> self = new AtomicReference<>();
    origin =
        new LocalHttpServer(
            rec ->
                rec.rawPath.equals("/landing")
                    ? notFound()
                    : LocalHttpServer.Reply.redirect(
                        302, "http://localhost:" + self.get().port() + "/landing"));
    self.set(origin);
    call(() -> client(origin.url(), null).paymentRequests().get("abc"));

    List<LocalHttpServer.Recorded> hops = origin.requests();
    assertEquals(2, hops.size());
    assertCredentialsPresent(hops.get(0), false);
    assertCredentialsAbsent(hops.get(1));
  }

  @Test
  void sameOriginRedirectKeepsCredentialHeaders() throws Exception {
    origin =
        new LocalHttpServer(
            rec ->
                rec.rawPath.equals("/landing")
                    ? notFound()
                    : LocalHttpServer.Reply.redirect(302, "/landing"));
    call(() -> clientWithExtraCredentials(origin.url()).paymentRequests().get("abc"));

    List<LocalHttpServer.Recorded> hops = origin.requests();
    assertEquals(2, hops.size());
    assertEquals("/landing", hops.get(1).rawPath);
    assertCredentialsPresent(hops.get(1), true);
  }

  @Test
  void credentialHeadersStayRemovedAfterReturningToOrigin() throws Exception {
    AtomicReference<LocalHttpServer> back = new AtomicReference<>();
    other =
        new LocalHttpServer(
            rec -> LocalHttpServer.Reply.redirect(302, back.get().url() + "/landing"));
    origin =
        new LocalHttpServer(
            rec ->
                rec.rawPath.equals("/landing")
                    ? notFound()
                    : LocalHttpServer.Reply.redirect(302, other.url() + "/hop"));
    back.set(origin);
    call(() -> clientWithExtraCredentials(origin.url()).paymentRequests().get("abc"));

    List<LocalHttpServer.Recorded> hops = origin.requests();
    assertEquals(2, hops.size());
    assertCredentialsPresent(hops.get(0), true);
    assertEquals("/landing", hops.get(1).rawPath);
    assertCredentialsAbsent(hops.get(1));
    assertCredentialsAbsent(other.requests().get(0));
  }

  private LocalHttpServer.Recorded postThenRedirect(int status) throws Exception {
    origin =
        new LocalHttpServer(
            rec ->
                rec.rawPath.equals("/landing")
                    ? notFound()
                    : LocalHttpServer.Reply.redirect(status, "/landing"));
    call(() -> client(origin.url(), null).paymentRequests().cancel("abc", "changed mind"));
    List<LocalHttpServer.Recorded> hops = origin.requests();
    assertEquals(2, hops.size());
    assertEquals("POST", hops.get(0).method);
    assertTrue(hops.get(0).body.contains("changed mind"));
    return hops.get(1);
  }

  @Test
  void status303SwitchesPostToGetWithoutBody() throws Exception {
    LocalHttpServer.Recorded hop = postThenRedirect(303);
    assertEquals("GET", hop.method);
    assertEquals("", hop.body);
    assertNull(hop.header("content-type"));
  }

  @Test
  void status302SwitchesPostToGetWithoutBody() throws Exception {
    LocalHttpServer.Recorded hop = postThenRedirect(302);
    assertEquals("GET", hop.method);
    assertEquals("", hop.body);
    assertNull(hop.header("content-type"));
  }

  @Test
  void status301SwitchesPostToGetWithoutBody() throws Exception {
    LocalHttpServer.Recorded hop = postThenRedirect(301);
    assertEquals("GET", hop.method);
    assertEquals("", hop.body);
    assertNull(hop.header("content-type"));
  }

  @Test
  void status307KeepsMethodAndBody() throws Exception {
    LocalHttpServer.Recorded hop = postThenRedirect(307);
    assertEquals("POST", hop.method);
    assertTrue(hop.body.contains("changed mind"));
    assertNotNull(hop.header("content-type"));
  }

  @Test
  void status308KeepsMethodAndBody() throws Exception {
    LocalHttpServer.Recorded hop = postThenRedirect(308);
    assertEquals("POST", hop.method);
    assertTrue(hop.body.contains("changed mind"));
    assertNotNull(hop.header("content-type"));
  }

  private void redirectChain(int redirects) throws Exception {
    Function<LocalHttpServer.Recorded, LocalHttpServer.Reply> handler =
        rec -> {
          int n = rec.rawPath.startsWith("/r") ? Integer.parseInt(rec.rawPath.substring(2)) : 0;
          return n >= redirects ? notFound() : LocalHttpServer.Reply.redirect(302, "/r" + (n + 1));
        };
    origin = new LocalHttpServer(handler);
  }

  @Test
  void fiveRedirectsAreFollowed() throws Exception {
    redirectChain(5);
    Throwable err = call(() -> client(origin.url(), null).paymentRequests().get("abc"));
    assertInstanceOf(APIException.class, err);
    assertEquals(6, origin.requests().size());
  }

  private void assertTooManyRedirects(Throwable err) {
    assertInstanceOf(ConnectionException.class, err);
    assertTrue(
        err.getMessage().contains("Too many redirects (more than 5)"),
        "unexpected message " + err.getMessage());
    assertEquals(6, origin.requests().size());
  }

  @Test
  void moreThanFiveRedirectsFailWithoutRetry() throws Exception {
    redirectChain(6);
    PayOS payOS = new PayOS(defaultRetryOptions(origin.url()));
    assertTooManyRedirects(call(() -> payOS.paymentRequests().get("abc")));
  }

  @Test
  void moreThanFiveRedirectsFailWithoutRetryOnDownload() throws Exception {
    redirectChain(6);
    PayOS payOS = new PayOS(defaultRetryOptions(origin.url()));
    assertTooManyRedirects(call(() -> payOS.paymentRequests().invoices().download("inv1", "pl1")));
  }

  @Test
  void moreThanFiveRedirectsFailWithoutRetryAsync() throws Exception {
    redirectChain(6);
    PayOSAsync payOS = new PayOSAsync(defaultRetryOptions(origin.url()));
    assertTooManyRedirects(callAsync(() -> payOS.paymentRequests().get("abc")));
  }

  @Test
  void moreThanFiveRedirectsFailWithoutRetryOnAsyncDownload() throws Exception {
    redirectChain(6);
    PayOSAsync payOS = new PayOSAsync(defaultRetryOptions(origin.url()));
    assertTooManyRedirects(
        callAsync(() -> payOS.paymentRequests().invoices().download("inv1", "pl1")));
  }

  @Test
  void onlyRedirectStatusesAreFollowed() throws Exception {
    for (int status : new int[] {300, 304, 305, 306, 399}) {
      try (LocalHttpServer target = new LocalHttpServer(rec -> notFound());
          LocalHttpServer source =
              new LocalHttpServer(
                  rec -> LocalHttpServer.Reply.redirect(status, target.url() + "/landing"))) {
        Throwable err = call(() -> client(source.url(), null).paymentRequests().get("abc"));

        assertInstanceOf(APIException.class, err, "status " + status);
        assertEquals(1, source.requests().size(), "status " + status);
        assertTrue(target.requests().isEmpty(), "status " + status + " must not be followed");
      }
    }
  }

  /** In-memory cookie jar seeded with a host-only cookie for 127.0.0.1 (any port). */
  private static final class MemoryCookieJar implements CookieJar {
    final List<Cookie> cookies = new ArrayList<>();

    MemoryCookieJar(String seedUrl) {
      HttpUrl url = HttpUrl.get(seedUrl);
      cookies.add(Cookie.parse(url, "session=jar"));
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
      List<Cookie> matching = new ArrayList<>();
      for (Cookie c : cookies) {
        if (c.matches(url)) {
          matching.add(c);
        }
      }
      return matching;
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> received) {
      cookies.addAll(received);
    }

    synchronized boolean has(String name) {
      return cookies.stream().anyMatch(c -> c.name().equals(name));
    }
  }

  private static PayOS clientWithJar(String baseUrl, CookieJar jar) {
    return client(baseUrl, new OkHttpClient.Builder().cookieJar(jar).build());
  }

  @Test
  void cookieJarCookiesStayOnSameOriginRedirect() throws Exception {
    origin =
        new LocalHttpServer(
            rec ->
                rec.rawPath.equals("/landing")
                    ? notFound()
                    : LocalHttpServer.Reply.redirect(302, "/landing"));
    MemoryCookieJar jar = new MemoryCookieJar(origin.url());
    call(() -> clientWithJar(origin.url(), jar).paymentRequests().get("abc"));

    List<LocalHttpServer.Recorded> hops = origin.requests();
    assertEquals(2, hops.size());
    assertEquals("session=jar", hops.get(0).header("cookie"));
    assertEquals("session=jar", hops.get(1).header("cookie"));
  }

  @Test
  void cookieJarCookiesAreNotSentAfterLeavingOrigin() throws Exception {
    other =
        new LocalHttpServer(
            rec -> LocalHttpServer.Reply.json(404, NOT_FOUND).header("Set-Cookie", "other=b"));
    origin =
        new LocalHttpServer(rec -> LocalHttpServer.Reply.redirect(302, other.url() + "/landing"));
    MemoryCookieJar jar = new MemoryCookieJar(origin.url());
    call(() -> clientWithJar(origin.url(), jar).paymentRequests().get("abc"));

    assertEquals("session=jar", origin.requests().get(0).header("cookie"));
    assertEquals(1, other.requests().size());
    assertCredentialsAbsent(other.requests().get(0));
    assertTrue(jar.has("other"), "cookies set after leaving the origin are still saved");
  }

  private void originToOtherAndBack() throws Exception {
    AtomicReference<LocalHttpServer> back = new AtomicReference<>();
    other =
        new LocalHttpServer(
            rec -> LocalHttpServer.Reply.redirect(302, back.get().url() + "/landing"));
    origin =
        new LocalHttpServer(
            rec ->
                rec.rawPath.equals("/landing")
                    ? notFound()
                    : LocalHttpServer.Reply.redirect(302, other.url() + "/hop"));
    back.set(origin);
  }

  private void assertNoJarCookieAfterReturning() {
    List<LocalHttpServer.Recorded> hops = origin.requests();
    assertEquals(2, hops.size());
    assertEquals("session=jar", hops.get(0).header("cookie"));
    assertCredentialsAbsent(other.requests().get(0));
    assertEquals("/landing", hops.get(1).rawPath);
    assertCredentialsAbsent(hops.get(1));
  }

  @Test
  void cookieJarCookiesStayOffAfterReturningToOrigin() throws Exception {
    originToOtherAndBack();
    MemoryCookieJar jar = new MemoryCookieJar(origin.url());
    call(() -> clientWithJar(origin.url(), jar).paymentRequests().get("abc"));
    assertNoJarCookieAfterReturning();
  }

  @Test
  void cookieJarCookiesStayOffAfterReturningToOriginAsync() throws Exception {
    originToOtherAndBack();
    MemoryCookieJar jar = new MemoryCookieJar(origin.url());
    PayOSAsync payOS =
        new PayOSAsync(
            ClientOptions.builder()
                .clientId("client-id")
                .apiKey("api-key")
                .checksumKey("checksum-key")
                .baseURL(origin.url())
                .maxRetries(0)
                .httpClient(new OkHttpClient.Builder().cookieJar(jar).build())
                .build());
    callAsync(() -> payOS.paymentRequests().get("abc"));
    assertNoJarCookieAfterReturning();
  }

  @Test
  void clientWithRedirectsDisabledDoesNotFollow() throws Exception {
    other = new LocalHttpServer(rec -> notFound());
    origin =
        new LocalHttpServer(rec -> LocalHttpServer.Reply.redirect(302, other.url() + "/landing"));
    OkHttpClient http = new OkHttpClient.Builder().followRedirects(false).build();
    Throwable err = call(() -> client(origin.url(), http).paymentRequests().get("abc"));

    assertInstanceOf(APIException.class, err);
    assertEquals(1, origin.requests().size());
    assertTrue(other.requests().isEmpty());
  }

  @Test
  void clientWithSslRedirectsDisabledDoesNotFollowSchemeChange() throws Exception {
    other = new LocalHttpServer(rec -> notFound());
    origin =
        new LocalHttpServer(
            rec -> LocalHttpServer.Reply.redirect(302, "https://127.0.0.1:" + other.port() + "/x"));
    OkHttpClient http = new OkHttpClient.Builder().followSslRedirects(false).build();
    Throwable err = call(() -> client(origin.url(), http).paymentRequests().get("abc"));

    assertInstanceOf(APIException.class, err);
    assertEquals(1, origin.requests().size());
    assertTrue(other.requests().isEmpty());
  }

  @Test
  void callerHeadersOnDefaultPathAreSentToOrigin() throws Exception {
    origin = new LocalHttpServer(rec -> notFound());
    Throwable err =
        assertThrows(
            PayOSException.class,
            () -> clientWithExtraCredentials(origin.url()).paymentRequests().get("abc"));
    assertNotNull(err);
    assertCredentialsPresent(origin.requests().get(0), true);
  }
}
