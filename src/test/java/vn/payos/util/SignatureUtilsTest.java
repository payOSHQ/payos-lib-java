package vn.payos.util;

import static org.junit.Assert.assertEquals;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import io.github.cdimascio.dotenv.Dotenv;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;
import vn.payos.type.Transaction;
import vn.payos.type.WebhookData;

public class SignatureUtilsTest {
  private static final Logger LOGGER = Logger.getLogger(SignatureUtilsTest.class.getName());

  private static String checksumKey;

  private static String webhookUrl;

  @BeforeClass
  public static void setup() {
    Dotenv env = Dotenv.load();
    checksumKey = env.get("PAYOS_CHECKSUM_KEY");
    webhookUrl = env.get("PAYOS_WEBHOOK_URL");
  }

  @Test
  public void testCreateSignatureFromObjOfCheckoutResponseData()
      throws NoSuchAlgorithmException, InvalidKeyException {
    try {
      Long orderCode = (long) 123;

      CheckoutResponseData checkoutResponseData = CheckoutResponseData.builder().bin("970422")
          .accountNumber("12345678").accountName("bankusrdemo1").amount(3000).description("VQRIO123")
          .orderCode(orderCode).currency("VND").paymentLinkId("124c33293c43417ab7879e14c8d9eb18")
          .status("PENDING").checkoutUrl("https://pay.payos.vn/web/124c33293c43417ab7879e14c8d9eb18")
          .qrCode("qrcodeThanh toan don hang").build();

      String signature = SignatureUtils.createSignatureFromObj(checkoutResponseData, checksumKey);
      String expectSignature = "0913c7f6a660cf950ba10b7d89c1b1d264a86913ff98801be6d37f2c3c81712f";

      LOGGER.info(signature);

      assertEquals(expectSignature, signature);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,
          "Exception occurred in testCreateSignatureFromObjOfCheckoutResponseData: " + e.getMessage(), e);
    }
  }

  @Test
  public void testCreateSignatureFromObjOfPaymentLinkData() throws NoSuchAlgorithmException, InvalidKeyException {
    try {
      Long orderCode = (long) 123;

      Transaction transaction1 = Transaction.builder().reference("FT24124874048804").amount(3000).accountNumber("")
          .description("FHGOWL89G")
          .transactionDateTime("2023-02-04 18:25:00").virtualAccountName(null).virtualAccountNumber(null)
          .counterAccountBankId(null).counterAccountBankName(null).counterAccountNumber(null).counterAccountName(null)
          .build();

      Transaction transaction2 = Transaction.builder().reference("FT24124874048804").amount(3000).accountNumber("")
          .description("FHGOWL89G")
          .transactionDateTime("2023-02-04 18:25:00").virtualAccountName(null).virtualAccountNumber(null)
          .counterAccountBankId(null).counterAccountBankName(null).counterAccountNumber(null).counterAccountName(null)
          .build();

      PaymentLinkData paymentLinkData = PaymentLinkData.builder().id("124c33293c43417ab7879e14c8d9eb18")
          .orderCode(orderCode).amount(3000).amountPaid(3000).amountRemaining(0).status("PAID")
          .createdAt("2023-02-04 18:25:00").transaction(transaction1).transaction(transaction2).canceledAt(null)
          .cancellationReason(null).build();

      String signature = SignatureUtils.createSignatureFromObj(paymentLinkData, checksumKey);

      String expectSignature = "5b1ba2947082cc99e6660d399b2f4fe4745ad6f99e17f5da831e174955d1b636";

      LOGGER.info(signature);

      assertEquals(expectSignature, signature);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,
          "Exception occurred in testCreateSignatureFromObjOfPaymentLinkData: " + e.getMessage(), e);
    }
  }

  @Test
  public void testCreateSignatureFromObjOfWebhookData() throws NoSuchAlgorithmException, InvalidKeyException {
    try {
      WebhookData webhookData = WebhookData.builder().orderCode((long) 123).amount(3000).description("VQRIO123")
          .accountNumber("12345678").reference("TF230204212323").transactionDateTime("2023-02-04 18:25:00")
          .currency("VND").paymentLinkId("124c33293c43417ab7879e14c8d9eb18").code("00").desc("Thành công")
          .build();

      String signature = SignatureUtils.createSignatureFromObj(webhookData, checksumKey);

      String expectSignature = "8b50051f80b534f8a54b457a6e0ed6847e07b138035ec22cea65a8a167fbbe14";

      LOGGER.info(signature);

      assertEquals(expectSignature, signature);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,
          "Exception occurred in testCreateSignatureFromObjOfWebhookData: " + e.getMessage(), e);
    }
  }

  @Test
  public void testCreateSignatureOfPaymentRequest() throws NoSuchAlgorithmException, InvalidKeyException {
    try {
      Long orderCode = (long) 670692;

      ItemData itemData = ItemData.builder().name("Mỳ tôm Hảo Hảo ly").quantity(1).price(2000).build();

      PaymentData paymentData = PaymentData.builder().orderCode(orderCode).amount(2000)
          .description("Thanh toán đơn hàng").returnUrl(webhookUrl + "/success").cancelUrl(webhookUrl + "/cancel")
          .item(itemData).build();

      String signature = SignatureUtils.createSignatureOfPaymentRequest(paymentData, checksumKey);

      String expectSignature = "71cde5c8a55f21a38e58f265d0ed3b2f9571cc9f974c80fe21f11c08355e7a31";

      LOGGER.info(signature);

      assertEquals(expectSignature, signature);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,
          "Exception occurred in testCreateSignatureOfPaymentRequest: " + e.getMessage(), e);
    }
  }
}