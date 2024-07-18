
package vn.payos;

import java.nio.charset.StandardCharsets;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.payos.constant.PayOSConstant;
import vn.payos.exception.PayOSException;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PayOSResponse;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;
import vn.payos.util.SignatureUtils;

/**
 * The PayOS class provides methods to interact with the payment channel.
 * 
 * To use the PayOS class, you need to provide the client ID, API key, and
 * checksum key of the payment channel you create at
 * <a href="https://my.payos.vn">PayOS</a>. Optionally, you can also provide a
 * partner code.
 */
public class PayOS {
  /**
   * Client ID of the PayOS payment channel
   */
  private final String clientId;

  /**
   * API Key of the PayOS payment channel
   */
  private final String apiKey;

  /**
   * Checksum Key of the PayOS payment channel
   */
  private final String checksumKey;

  /**
   * Your Partner Code
   */
  private final String partnerCode;

  private static final String PAYOS_BASE_URL = PayOSConstant.PAYOS_BASE_URL;

  /**
   * Create a payOS object to use payment channel methods. Credentials are fields
   * provided after creating a payOS payment channel at
   * <a href="https://my.payos.vn">PayOS</a>
   * 
   * @param clientId    Client ID of the PayOS payment channel
   * @param apiKey      API Key of the PayOS payment channel
   * @param checksumKey Checksum Key of the PayOS payment channel
   */
  public PayOS(String clientId, String apiKey, String checksumKey) {
    this.clientId = clientId;
    this.apiKey = apiKey;
    this.checksumKey = checksumKey;
    this.partnerCode = null;
  }

  /**
   * Create a payOS object to use payment channel methods. Credentials are fields
   * provided after creating a payOS payment channel at
   * <a href="https://my.payos.vn">PayOS</a>
   * 
   * @param clientId    Client ID of the PayOS payment channel
   * @param apiKey      API Key of the PayOS payment channel
   * @param checksumKey Checksum Key of the PayOS payment channel
   * @param partnerCode Your Partner Code
   */
  public PayOS(String clientId, String apiKey, String checksumKey, String partnerCode) {
    this.clientId = clientId;
    this.apiKey = apiKey;
    this.checksumKey = checksumKey;
    this.partnerCode = partnerCode;
  }

  /**
   * Create payment link for the order data passed in the parameter
   * 
   * @param paymentData Payment data
   * @return Checkout order data
   * @throws Exception Create payment link failed
   */
  public CheckoutResponseData createPaymentLink(PaymentData paymentData)
      throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    String url = PAYOS_BASE_URL + "/v2/payment-requests";
    CloseableHttpClient client = HttpClients.createDefault();
    final HttpPost httpPost = new HttpPost(url);
    paymentData.setSignature(SignatureUtils.createSignatureOfPaymentRequest(paymentData, checksumKey));
    httpPost.setHeader("Accept", "application/json");
    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("Charset", "UTF-8");
    httpPost.setHeader("x-client-id", clientId);
    httpPost.setHeader("x-api-key", apiKey);
    if (this.partnerCode != null) {
      httpPost.setHeader("x-partner-code", this.partnerCode);
    }
    String paymentDataJson = objectMapper.writeValueAsString(paymentData);
    httpPost.setEntity(new StringEntity(paymentDataJson, StandardCharsets.UTF_8));
    CloseableHttpResponse response = client.execute(httpPost);
    HttpEntity entity = response.getEntity();
    if (entity == null) {
      response.close();
      client.close();
      throw new Exception("Call api failed!");
    }
    String responseData = EntityUtils.toString(entity);
    response.close();
    client.close();
    PayOSResponse<CheckoutResponseData> res = objectMapper.readValue(responseData,
        new TypeReference<PayOSResponse<CheckoutResponseData>>() {
        });

    if (!res.getCode().equals("00")) {
      throw new PayOSException(res.getCode(), res.getDesc());
    }

    String paymentLinkResSignature = SignatureUtils.createSignatureFromObj(res.getData(), checksumKey);
    if (!paymentLinkResSignature.equals(res.getSignature())) {
      throw new Exception(PayOSConstant.ERROR_MESSAGE.get("DATA_NOT_INTEGRITY"));
    }

    return res.getData();
  }

  /**
   * Get payment information of an order that has created a payment link
   * 
   * @param orderId Order ID
   * @return Payment link information
   * @throws Exception Get payment link information failed
   */
  public PaymentLinkData getPaymentLinkInformation(Long orderId) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    String url = PAYOS_BASE_URL + "/v2/payment-requests/" + orderId;
    CloseableHttpClient client = HttpClients.createDefault();
    final HttpGet httpGet = new HttpGet(url);
    httpGet.setHeader("Accept", "application/json");
    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Charset", "UTF-8");
    httpGet.setHeader("x-client-id", clientId);
    httpGet.setHeader("x-api-key", apiKey);
    CloseableHttpResponse response = client.execute(httpGet);

    HttpEntity entity = response.getEntity();
    if (entity == null) {
      response.close();
      client.close();
      throw new Exception("Call api failed!");
    }
    String responseData = EntityUtils.toString(entity);
    response.close();
    client.close();
    PayOSResponse<PaymentLinkData> res = objectMapper.readValue(responseData,
        new TypeReference<PayOSResponse<PaymentLinkData>>() {
        });

    if (!res.getCode().equals("00")) {
      throw new PayOSException(res.getCode(), res.getDesc());
    }

    String paymentLinkResSignature = SignatureUtils.createSignatureFromObj(res.getData(), checksumKey);
    if (!paymentLinkResSignature.equals(res.getSignature())) {
      throw new Exception(PayOSConstant.ERROR_MESSAGE.get("DATA_NOT_INTEGRITY"));
    }

    return res.getData();
  }

  /**
   * Validate the Webhook URL of a payment channel and add or update Webhook URL
   * for that payment channel if successful
   * 
   * @param webhookUrl Webhook URL
   * @return Webhook URL
   * @throws Exception Confirm webhook failed
   */
  public String confirmWebhook(String webhookUrl) throws Exception {
    if (webhookUrl == null || webhookUrl.isEmpty()) {
      throw new Exception(PayOSConstant.ERROR_MESSAGE.get("INVALID_PARAMETER"));
    }
    String url = PAYOS_BASE_URL + "/confirm-webhook";
    CloseableHttpClient client = HttpClients.createDefault();
    final HttpPost httpPost = new HttpPost(url);
    httpPost.setHeader("Accept", "application/json");
    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("Charset", "UTF-8");
    httpPost.setHeader("x-client-id", clientId);
    httpPost.setHeader("x-api-key", apiKey);
    httpPost.setEntity(new StringEntity("{\"webhookUrl\":\"" + webhookUrl + "\"}", StandardCharsets.UTF_8));
    CloseableHttpResponse response = client.execute(httpPost);
    int statusCode = response.getCode();
    response.close();
    client.close();

    if (statusCode == 200) {
      return webhookUrl;
    } else if (statusCode == 404) {
      throw new PayOSException(PayOSConstant.ERROR_CODE.get("INTERNAL_SERVER_ERROR"),
          PayOSConstant.ERROR_MESSAGE.get("INTERNAL_SERVER_ERROR"));
    } else if (statusCode == 401) {
      throw new PayOSException(PayOSConstant.ERROR_CODE.get("UNAUTHORIZED"),
          PayOSConstant.ERROR_MESSAGE.get("UNAUTHORIZED"));
    }
    throw new PayOSException(PayOSConstant.ERROR_CODE.get("INTERNAL_SERVER_ERROR"),
        PayOSConstant.ERROR_MESSAGE.get("INTERNAL_SERVER_ERROR"));
  }

  /**
   * Cancel payment link of an order
   * 
   * @param orderId            Order ID
   * @param cancellationReason Reason for cancelling order (optional)
   * @return Payment link information
   * @throws Exception Cancel payment link failed
   */
  public PaymentLinkData cancelPaymentLink(long orderId, String cancellationReason)
      throws Exception {
    String url = PAYOS_BASE_URL + "/v2/payment-requests/" + orderId + "/cancel";
    ObjectMapper objectMapper = new ObjectMapper();
    CloseableHttpClient client = HttpClients.createDefault();
    final HttpPost httpPost = new HttpPost(url);
    httpPost.setHeader("Accept", "application/json");
    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("Charset", "UTF-8");
    httpPost.setHeader("x-client-id", clientId);
    httpPost.setHeader("x-api-key", apiKey);
    if (cancellationReason != null)
      httpPost
          .setEntity(
              new StringEntity("{\"cancellationReason\":\"" + cancellationReason + "\"}", StandardCharsets.UTF_8));
    CloseableHttpResponse response = client.execute(httpPost);
    int statusCode = response.getCode();
    if (statusCode != 200) {
      throw new PayOSException(PayOSConstant.ERROR_CODE.get("INTERNAL_SERVER_ERROR"),
          PayOSConstant.ERROR_MESSAGE.get("INTERNAL_SERVER_ERROR"));
    }
    HttpEntity entity = response.getEntity();
    if (entity == null) {
      response.close();
      client.close();
      throw new Exception("Call api failed!");
    }
    String responseData = EntityUtils.toString(entity);
    response.close();
    client.close();
    PayOSResponse<PaymentLinkData> res = objectMapper.readValue(responseData,
        new TypeReference<PayOSResponse<PaymentLinkData>>() {
        });

    if (!res.getCode().equals("00")) {
      throw new PayOSException(res.getCode(), res.getDesc());
    }

    String paymentLinkResSignature = SignatureUtils.createSignatureFromObj(res.getData(), checksumKey);
    if (!paymentLinkResSignature.equals(res.getSignature())) {
      throw new Exception(PayOSConstant.ERROR_MESSAGE.get("DATA_NOT_INTEGRITY"));
    }

    return res.getData();

  }

  /**
   * Verify data received via webhook after payment
   * 
   * @param webhookBody Request body received from webhook
   * @return Webhook data
   * @throws Exception Verify webhook data failed
   */
  public WebhookData verifyPaymentWebhookData(Webhook webhookBody) throws Exception {
    WebhookData data = webhookBody.getData();
    String signature = webhookBody.getSignature();
    if (signature.isEmpty()) {
      throw new Exception(PayOSConstant.ERROR_MESSAGE.get("NO_SIGNATURE"));
    }
    String signData = SignatureUtils.createSignatureFromObj(data, checksumKey);
    if (!signData.equals(signature)) {
      throw new Exception(PayOSConstant.ERROR_MESSAGE.get("DATA_NOT_INTEGRITY"));
    }
    return data;
  }
}
