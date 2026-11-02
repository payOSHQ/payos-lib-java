package vn.payos.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vn.payos.PayOS;
import vn.payos.PayOSAsync;
import vn.payos.exception.PayOSException;

/** Identifiers interpolated into API paths are sent as one percent-encoded path segment. */
public class PathSegmentEncodingTest {
  private LocalHttpServer server;
  private PayOS payOS;
  private PayOSAsync payOSAsync;

  /** A public method that interpolates one identifier, with its expected path template. */
  private static final class Call {
    final String name;
    final String template;
    final String param;
    final Function<String, Object> invoke;

    Call(String name, String template, String param, Function<String, Object> invoke) {
      this.name = name;
      this.template = template;
      this.param = param;
      this.invoke = invoke;
    }
  }

  /** Same for numeric order codes. */
  private static final class NumericCall {
    final String name;
    final String template;
    final Function<Long, Object> invoke;

    NumericCall(String name, String template, Function<Long, Object> invoke) {
      this.name = name;
      this.template = template;
      this.invoke = invoke;
    }
  }

  @BeforeEach
  void setUp() throws Exception {
    server =
        new LocalHttpServer(
            rec -> LocalHttpServer.Reply.json(404, "{\"code\":\"101\",\"desc\":\"not found\"}"));
    ClientOptions options =
        ClientOptions.builder()
            .clientId("client-id")
            .apiKey("api-key")
            .checksumKey("checksum-key")
            .baseURL(server.url())
            .maxRetries(0)
            .build();
    payOS = new PayOS(options);
    payOSAsync = new PayOSAsync(options);
  }

  @AfterEach
  void tearDown() {
    server.close();
  }

  private List<Call> stringCalls() {
    List<Call> calls = new ArrayList<>();
    calls.add(
        new Call(
            "paymentRequests.get",
            "/v2/payment-requests/%s",
            "paymentLinkId",
            id -> payOS.paymentRequests().get(id)));
    calls.add(
        new Call(
            "paymentRequests.cancel",
            "/v2/payment-requests/%s/cancel",
            "paymentLinkId",
            id -> payOS.paymentRequests().cancel(id)));
    calls.add(
        new Call(
            "paymentRequests.cancel(reason)",
            "/v2/payment-requests/%s/cancel",
            "paymentLinkId",
            id -> payOS.paymentRequests().cancel(id, "reason")));
    calls.add(
        new Call(
            "invoices.get",
            "/v2/payment-requests/%s/invoices",
            "paymentLinkId",
            id -> payOS.paymentRequests().invoices().get(id)));
    calls.add(
        new Call(
            "invoices.download(paymentLinkId)",
            "/v2/payment-requests/%s/invoices/inv1/download",
            "paymentLinkId",
            id -> payOS.paymentRequests().invoices().download("inv1", id)));
    calls.add(
        new Call(
            "invoices.download(invoiceId)",
            "/v2/payment-requests/pl1/invoices/%s/download",
            "invoiceId",
            id -> payOS.paymentRequests().invoices().download(id, "pl1")));
    calls.add(
        new Call(
            "invoices.download(invoiceId, orderCode)",
            "/v2/payment-requests/123/invoices/%s/download",
            "invoiceId",
            id -> payOS.paymentRequests().invoices().download(id, 123L)));
    calls.add(new Call("payouts.get", "/v1/payouts/%s", "payoutId", id -> payOS.payouts().get(id)));
    calls.add(
        new Call(
            "async paymentRequests.get",
            "/v2/payment-requests/%s",
            "paymentLinkId",
            id -> payOSAsync.paymentRequests().get(id)));
    calls.add(
        new Call(
            "async paymentRequests.cancel",
            "/v2/payment-requests/%s/cancel",
            "paymentLinkId",
            id -> payOSAsync.paymentRequests().cancel(id)));
    calls.add(
        new Call(
            "async paymentRequests.cancel(reason)",
            "/v2/payment-requests/%s/cancel",
            "paymentLinkId",
            id -> payOSAsync.paymentRequests().cancel(id, "reason")));
    calls.add(
        new Call(
            "async invoices.get",
            "/v2/payment-requests/%s/invoices",
            "paymentLinkId",
            id -> payOSAsync.paymentRequests().invoices().get(id)));
    calls.add(
        new Call(
            "async invoices.download(paymentLinkId)",
            "/v2/payment-requests/%s/invoices/inv1/download",
            "paymentLinkId",
            id -> payOSAsync.paymentRequests().invoices().download("inv1", id)));
    calls.add(
        new Call(
            "async invoices.download(invoiceId)",
            "/v2/payment-requests/pl1/invoices/%s/download",
            "invoiceId",
            id -> payOSAsync.paymentRequests().invoices().download(id, "pl1")));
    calls.add(
        new Call(
            "async invoices.download(invoiceId, orderCode)",
            "/v2/payment-requests/123/invoices/%s/download",
            "invoiceId",
            id -> payOSAsync.paymentRequests().invoices().download(id, 123L)));
    calls.add(
        new Call(
            "async payouts.get", "/v1/payouts/%s", "payoutId", id -> payOSAsync.payouts().get(id)));
    return calls;
  }

  private List<NumericCall> numericCalls() {
    List<NumericCall> calls = new ArrayList<>();
    calls.add(
        new NumericCall(
            "paymentRequests.get(orderCode)",
            "/v2/payment-requests/%s",
            n -> payOS.paymentRequests().get(n)));
    calls.add(
        new NumericCall(
            "paymentRequests.cancel(orderCode)",
            "/v2/payment-requests/%s/cancel",
            n -> payOS.paymentRequests().cancel(n)));
    calls.add(
        new NumericCall(
            "invoices.get(orderCode)",
            "/v2/payment-requests/%s/invoices",
            n -> payOS.paymentRequests().invoices().get(n)));
    calls.add(
        new NumericCall(
            "invoices.download(orderCode)",
            "/v2/payment-requests/%s/invoices/inv1/download",
            n -> payOS.paymentRequests().invoices().download("inv1", n)));
    calls.add(
        new NumericCall(
            "async paymentRequests.get(orderCode)",
            "/v2/payment-requests/%s",
            n -> payOSAsync.paymentRequests().get(n)));
    calls.add(
        new NumericCall(
            "async paymentRequests.cancel(orderCode)",
            "/v2/payment-requests/%s/cancel",
            n -> payOSAsync.paymentRequests().cancel(n)));
    calls.add(
        new NumericCall(
            "async invoices.get(orderCode)",
            "/v2/payment-requests/%s/invoices",
            n -> payOSAsync.paymentRequests().invoices().get(n)));
    calls.add(
        new NumericCall(
            "async invoices.download(orderCode)",
            "/v2/payment-requests/%s/invoices/inv1/download",
            n -> payOSAsync.paymentRequests().invoices().download("inv1", n)));
    return calls;
  }

  /** Invoke the call and return the thrown exception, or null; async failures are unwrapped. */
  private static Throwable run(Function<Object, Object> call) {
    try {
      Object result = call.apply(null);
      if (result instanceof CompletableFuture) {
        ((CompletableFuture<?>) result).join();
      }
      return null;
    } catch (CompletionException e) {
      return e.getCause() != null ? e.getCause() : e;
    } catch (RuntimeException e) {
      return e;
    }
  }

  private String sentPath(Function<Object, Object> call) {
    int before = server.requests().size();
    run(call);
    List<LocalHttpServer.Recorded> requests = server.requests();
    assertEquals(before + 1, requests.size(), "exactly one request expected");
    return requests.get(requests.size() - 1).rawPath;
  }

  private void assertEncoded(String id, String encoded) {
    for (Call c : stringCalls()) {
      String path = sentPath(ignored -> c.invoke.apply(id));
      assertEquals(String.format(c.template, encoded), path, c.name + " with id " + id);
    }
  }

  @Test
  void plainIdIsSentUnchanged() {
    assertEncoded("abc123", "abc123");
  }

  @Test
  void dotSegmentPrefixIsEncodedAsOneSegment() {
    assertEncoded("../x", "..%2Fx");
  }

  @Test
  void slashIsEncoded() {
    assertEncoded("a/b", "a%2Fb");
  }

  @Test
  void questionMarkIsEncoded() {
    assertEncoded("a?b", "a%3Fb");
  }

  @Test
  void hashIsEncoded() {
    assertEncoded("a#b", "a%23b");
  }

  @Test
  void percentEncodedInputIsEncodedOnce() {
    assertEncoded("a%2Fb", "a%252Fb");
  }

  @Test
  void dotAndEmptyIdsAreRejectedWithoutRequest() {
    String[][] cases = {
      {".", "must not be a dot segment"},
      {"..", "must not be a dot segment"},
      {"", "must be a non-empty string or a safe integer"},
    };
    for (String[] tc : cases) {
      for (Call c : stringCalls()) {
        Throwable err = run(ignored -> c.invoke.apply(tc[0]));
        assertNotNull(err, c.name + " should reject '" + tc[0] + "'");
        assertInstanceOf(PayOSException.class, err, c.name);
        assertEquals(c.param + " " + tc[1], err.getMessage(), c.name);
      }
    }
    assertTrue(server.requests().isEmpty(), "no request expected");
  }

  @Test
  void validOrderCodeIsSentAsDecimal() {
    for (NumericCall c : numericCalls()) {
      String path = sentPath(ignored -> c.invoke.apply(123L));
      assertEquals(String.format(c.template, "123"), path, c.name);
    }
    for (NumericCall c : numericCalls()) {
      String path = sentPath(ignored -> c.invoke.apply(9007199254740991L));
      assertEquals(String.format(c.template, "9007199254740991"), path, c.name);
    }
  }

  @Test
  void negativeOrderCodeIsRejectedWithoutRequest() {
    for (NumericCall c : numericCalls()) {
      Throwable err = run(ignored -> c.invoke.apply(-1L));
      assertNotNull(err, c.name + " should reject -1");
      assertInstanceOf(PayOSException.class, err, c.name);
      assertEquals("orderCode must be a non-negative safe integer", err.getMessage(), c.name);
    }
    assertTrue(server.requests().isEmpty(), "no request expected");
  }

  @Test
  void orderCodeAboveSafeRangeIsRejectedWithoutRequest() {
    for (NumericCall c : numericCalls()) {
      Throwable err = run(ignored -> c.invoke.apply(9007199254740992L));
      assertNotNull(err, c.name + " should reject 2^53");
    }
    assertTrue(server.requests().isEmpty(), "no request expected");
  }
}
