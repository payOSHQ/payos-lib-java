package vn.payos.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vn.payos.PayOS;
import vn.payos.PayOSAsync;
import vn.payos.crypto.CryptoProviderImpl;
import vn.payos.exception.InvalidSignatureException;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.v2.paymentRequests.PaymentLinkStatus;
import vn.payos.model.v2.paymentRequests.invoices.InvoicesInfo;

/** Body-signed response data is shape-checked before its signature is verified. */
public class SignedResponseDataTest {
  private static final String CHECKSUM_KEY =
      "1a54716c8f0efb2744fb28b6e38b25da7f67a925d98bc1c18bd8faaecadd7675";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final String PAYMENT_LINK_DATA =
      "{\"id\":\"0c1f0b5b8a7c4a3fa3e5d1c2b4a69788\",\"orderCode\":1730000000123,"
          + "\"amount\":10000,\"amountPaid\":10000,\"amountRemaining\":0,\"status\":\"PAID\","
          + "\"createdAt\":\"2025-10-01T10:15:30+07:00\",\"transactions\":["
          + "{\"reference\":\"FT25274123456\",\"amount\":10000,\"accountNumber\":\"12345678\","
          + "\"description\":\"CSWM5R0Y Thanh toan don hang 123\","
          + "\"transactionDateTime\":\"2025-10-01 10:16:02\",\"virtualAccountName\":null,"
          + "\"virtualAccountNumber\":null,\"counterAccountBankId\":\"970422\","
          + "\"counterAccountBankName\":\"MB Bank\",\"counterAccountName\":\"NGUYEN VAN A\","
          + "\"counterAccountNumber\":\"0123456789\"}],"
          + "\"cancellationReason\":null,\"canceledAt\":null}";

  private static final String INVOICES_DATA =
      "{\"invoices\":[{\"invoiceId\":\"6f1c2d3e\",\"invoiceNumber\":\"00000123\","
          + "\"issuedTimestamp\":1759288562,\"issuedDatetime\":\"2025-10-01 10:16:02\","
          + "\"transactionId\":\"FT25274123456\",\"reservationCode\":\"ABC123\","
          + "\"codeOfTax\":\"M1-25-ABCDE-12345678901\"}]}";

  private final AtomicReference<String> responseBody = new AtomicReference<>();
  private LocalHttpServer server;
  private PayOS payOS;
  private PayOSAsync payOSAsync;

  @BeforeEach
  void setUp() throws Exception {
    server = new LocalHttpServer(rec -> LocalHttpServer.Reply.json(200, responseBody.get()));
    ClientOptions options =
        ClientOptions.builder()
            .clientId("client-id")
            .apiKey("api-key")
            .checksumKey(CHECKSUM_KEY)
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

  /** Respond with dataJson signed by the SDK's own helper over the parsed data. */
  private void respondSigned(String dataJson) throws Exception {
    Map<String, Object> parsed =
        MAPPER.readValue(dataJson, new TypeReference<Map<String, Object>>() {});
    String signature = new CryptoProviderImpl().createSignatureFromObj(parsed, CHECKSUM_KEY);
    assertNotNull(signature);
    responseBody.set(
        "{\"code\":\"00\",\"desc\":\"success\",\"data\":"
            + dataJson
            + ",\"signature\":\""
            + signature
            + "\"}");
  }

  private void assertRejected(String dataJson, String reason) throws Exception {
    respondSigned(dataJson);
    assertRejectedResponse(reason);
  }

  private void assertRejectedResponse(String reason) {
    InvalidSignatureException err =
        assertThrows(InvalidSignatureException.class, () -> payOS.paymentRequests().get("abc"));
    assertEquals("Invalid response data: " + reason, err.getMessage());

    CompletionException async =
        assertThrows(
            CompletionException.class, () -> payOSAsync.paymentRequests().get("abc").join());
    assertInstanceOf(InvalidSignatureException.class, async.getCause());
    assertEquals("Invalid response data: " + reason, async.getCause().getMessage());
  }

  @Test
  void signedPaymentLinkIsAccepted() throws Exception {
    respondSigned(PAYMENT_LINK_DATA);
    PaymentLink link = payOS.paymentRequests().get(1730000000123L);
    assertEquals("0c1f0b5b8a7c4a3fa3e5d1c2b4a69788", link.getId());
    assertEquals(PaymentLinkStatus.PAID, link.getStatus());
    assertEquals(1, link.getTransactions().size());
    assertEquals("FT25274123456", link.getTransactions().get(0).getReference());

    PaymentLink asyncLink = payOSAsync.paymentRequests().get("abc").join();
    assertEquals(10000L, asyncLink.getAmount());
  }

  @Test
  void signedInvoicesAreAccepted() throws Exception {
    respondSigned(INVOICES_DATA);
    InvoicesInfo info = payOS.paymentRequests().invoices().get("abc");
    assertEquals("00000123", info.getInvoices().get(0).getInvoiceNumber());
    assertEquals(
        "6f1c2d3e",
        payOSAsync
            .paymentRequests()
            .invoices()
            .get("abc")
            .join()
            .getInvoices()
            .get(0)
            .getInvoiceId());
  }

  @Test
  void ampersandKeySequenceInStringIsRejected() throws Exception {
    assertRejected(
        "{\"id\":\"abc\",\"status\":\"PENDING&amount=1\",\"amount\":10000}",
        "\"status\" must not contain a \"&key=\" sequence");
  }

  @Test
  void ampersandKeySequenceInListEntryIsRejected() throws Exception {
    assertRejected(
        "{\"id\":\"abc\",\"transactions\":[{\"reference\":\"r&amount=1\"}]}",
        "\"transactions[0].reference\" must not contain a \"&key=\" sequence");
  }

  @Test
  void plainAmpersandInStringIsAccepted() throws Exception {
    respondSigned("{\"id\":\"abc\",\"cancellationReason\":\"A & B = C\"}");
    assertEquals("A & B = C", payOS.paymentRequests().get("abc").getCancellationReason());
  }

  @Test
  void keyWithDelimiterIsRejected() throws Exception {
    assertRejected("{\"id\":\"abc\",\"a=b\":\"x\"}", "unsupported key \"a=b\"");
  }

  @Test
  void keyWithDelimiterInListEntryIsRejected() throws Exception {
    assertRejected(
        "{\"id\":\"abc\",\"transactions\":[{\"a&b\":\"x\"}]}",
        "unsupported key \"transactions[0].a&b\"");
  }

  @Test
  void protoKeyIsRejected() throws Exception {
    assertRejected("{\"id\":\"abc\",\"__proto__\":\"x\"}", "unsupported key \"__proto__\"");
  }

  @Test
  void topLevelObjectValueIsRejected() throws Exception {
    assertRejected("{\"id\":\"abc\",\"meta\":{\"a\":1}}", "\"meta\" has an unsupported type");
  }

  @Test
  void objectInsideListEntryIsRejected() throws Exception {
    assertRejected(
        "{\"id\":\"abc\",\"transactions\":[{\"meta\":{\"a\":1}}]}",
        "\"transactions[0].meta\" has an unsupported type");
  }

  @Test
  void listInsideListIsRejected() throws Exception {
    // The signing helper cannot sign nested lists, so any signature is used: the shape check
    // runs before the signature comparison.
    responseBody.set(
        "{\"code\":\"00\",\"desc\":\"success\",\"data\":{\"id\":\"abc\",\"transactions\":[[1,2]]},"
            + "\"signature\":\"00\"}");
    assertRejectedResponse("\"transactions[0]\" has an unsupported type");
  }

  @Test
  void unsafeIntegerIsRejected() throws Exception {
    assertRejected(
        "{\"id\":\"abc\",\"amount\":9007199254740993}", "\"amount\" must be a safe integer");
  }

  @Test
  void hugeIntegerIsRejected() throws Exception {
    assertRejected(
        "{\"id\":\"abc\",\"amount\":123456789012345678901234567890}",
        "\"amount\" must be a safe integer");
  }

  @Test
  void longMinValueIsRejected() throws Exception {
    assertRejected(
        "{\"id\":\"abc\",\"amount\":-9223372036854775808}", "\"amount\" must be a safe integer");
  }

  @Test
  void negativeUnsafeIntegerIsRejected() throws Exception {
    assertRejected(
        "{\"id\":\"abc\",\"amount\":-9007199254740992}", "\"amount\" must be a safe integer");
  }

  @Test
  void nonFiniteNumberIsRejected() throws Exception {
    assertRejected(
        "{\"id\":\"abc\",\"amount\":1e400}",
        "\"amount\" must be a finite number within the safe integer range");
  }

  @Test
  void unsafeIntegralDoubleIsRejected() throws Exception {
    assertRejected(
        "{\"id\":\"abc\",\"amount\":1e20}",
        "\"amount\" must be a finite number within the safe integer range");
  }
}
