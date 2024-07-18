package vn.payos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import io.github.cdimascio.dotenv.Dotenv;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;
import vn.payos.util.SignatureUtils;

public class PayOSTest {
  private static final Logger LOGGER = Logger.getLogger(PayOSTest.class.getName());

  private static String webhookUrl;

  private static PayOS payOS;

  @BeforeClass
  public static void setUp() {
    Dotenv env = Dotenv.load();
    String clientId = env.get("PAYOS_CLIENT_ID");
    String apiKey = env.get("PAYOS_API_KEY");
    String checksumKey = env.get("PAYOS_CHECKSUM_KEY");
    webhookUrl = env.get("PAYOS_WEBHOOK_URL");

    payOS = new PayOS(clientId, apiKey, checksumKey);
  }

  @Test
  public void testCreatePaymentLink() throws Exception {
    try {
      Long orderCode = System.currentTimeMillis() / 1000;
      ItemData itemData = ItemData.builder().name("Mỳ tôm Hảo Hảo ly").quantity(1).price(2000).build();

      PaymentData paymentData = PaymentData.builder().orderCode(orderCode).amount(2000)
          .description("Thanh toán đơn hàng").returnUrl(webhookUrl + "/success").cancelUrl(webhookUrl + "/cancel")
          .item(itemData).build();

      CheckoutResponseData result = payOS.createPaymentLink(paymentData);

      assertNotNull(result);
      assertEquals(orderCode, result.getOrderCode());
      LOGGER.info(result.getCheckoutUrl());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Exception occurred in testCreatePaymentLink: " + e.getMessage(), e);
      throw e;
    }

  }

  @Test
  public void testGetPaymentLinkInformation() throws Exception {
    try {
      Long orderCode = System.currentTimeMillis() / 1000;
      ItemData itemData = ItemData.builder().name("Mỳ tôm Hảo Hảo ly").quantity(1).price(2000).build();

      PaymentData paymentData = PaymentData.builder().orderCode(orderCode).amount(2000)
          .description("Thanh toán đơn hàng").returnUrl(webhookUrl + "/success").cancelUrl(webhookUrl + "/cancel")
          .item(itemData).build();

      CheckoutResponseData checkoutResponseData = payOS.createPaymentLink(paymentData);

      PaymentLinkData result = payOS.getPaymentLinkInformation(orderCode);

      assertEquals(checkoutResponseData.getOrderCode(), result.getOrderCode());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Exception occurred in testGetPaymentLinkInformation: " + e.getMessage(), e);
      throw e;
    }
  }

  @Test
  public void testCancelPaymentLinkInformation() throws Exception {
    try {
      Long orderCode = System.currentTimeMillis() / 1000;
      ItemData itemData = ItemData.builder().name("Mỳ tôm Hảo Hảo ly").quantity(1).price(2000).build();

      PaymentData paymentData = PaymentData.builder().orderCode(orderCode).amount(2000)
          .description("Thanh toán đơn hàng").returnUrl(webhookUrl + "/success").cancelUrl(webhookUrl + "/cancel")
          .item(itemData).build();

      CheckoutResponseData checkoutResponseData = payOS.createPaymentLink(paymentData);

      PaymentLinkData result = payOS.cancelPaymentLink(orderCode, "Cancel order");

      assertEquals(checkoutResponseData.getOrderCode(), result.getOrderCode());
      assertEquals("CANCELLED", result.getStatus());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Exception occurred in testCancelPaymentLinkInformation: " + e.getMessage(), e);
      throw e;
    }
  }

  @Test
  public void testConfirmWebhook() throws Exception {
    try {
      String result = payOS.confirmWebhook(webhookUrl);

      assertEquals(webhookUrl, result);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Exception occurred in testConfirmWebhook: " + e.getMessage(), e);
      throw e;
    }
  }

  @Test
  public void testVerifyWebhookData() throws Exception {
    try {
      Dotenv env = Dotenv.load();
      WebhookData webhookData = WebhookData.builder().orderCode((long) 123).amount(3000).description("VQRIO123")
          .accountNumber("12345678").reference("TF230204212323").transactionDateTime("2023-02-04 18:25:00")
          .currency("VND").paymentLinkId("124c33293c43417ab7879e14c8d9eb18").code("00").desc("Thành công").build();

      String signature = SignatureUtils.createSignatureFromObj(webhookData, env.get("PAYOS_CHECKSUM_KEY"));

      Webhook webhookBody = Webhook.builder().code("00").desc("success").data(webhookData)
          .signature(signature).build();

      WebhookData result = payOS.verifyPaymentWebhookData(webhookBody);

      assertNotNull(result);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Exception occurred in testVerifyWebhookData: " + e.getMessage(), e);
      throw e;
    }
  }

}