package com.lib.payos;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lib.payos.type.PaymentData;
import java.util.Objects;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class PayOS {
  private final String clientId;
  private final String apiKey;
  private final String checksumKey;
  private static final String PAYOS_BASE_URL = Constants.PAYOS_BASE_URL;

  public PayOS(String clientId, String apiKey, String checksumKey) {
    this.clientId = clientId;
    this.apiKey = apiKey;
    this.checksumKey = checksumKey;
  }
  public JsonNode createPaymentLink(PaymentData paymentData)
      throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    String url = PAYOS_BASE_URL + "/v2/payment-requests";
    CloseableHttpClient client = HttpClients.createDefault();
    final HttpPost httpPost = new HttpPost(url);
    paymentData.setSignature(Utils.createSignatureOfPaymentRequest(paymentData, checksumKey));
    httpPost.setHeader("Accept", "application/json");
    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("x-client-id", clientId);
    httpPost.setHeader("x-api-key", apiKey);
    String paymentDataJson = objectMapper.writeValueAsString(paymentData);
    httpPost.setEntity(new StringEntity(paymentDataJson));
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
    JsonNode res =  objectMapper.readTree(responseData);
    if (!Objects.equals(res.get("code").asText(), "00")) {
      throw new PayOSError(res.get("code").asText(), res.get("desc").asText() );
    }
    String paymentLinkResSignature = Utils.createSignatureFromObj(res.get("data"), checksumKey);
    if (!paymentLinkResSignature.equals(res.get("signature").asText())) {
      throw new Exception(Constants.ERROR_MESSAGE.get("DATA_NOT_INTEGRITY"));
    }
    return res.get("data");
  }
  public JsonNode getPaymentLinkInfomation(int orderId) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    String url = PAYOS_BASE_URL + "/v2/payment-requests/" +orderId;
    CloseableHttpClient client = HttpClients.createDefault();
    final HttpGet httpGet = new HttpGet(url);
    httpGet.setHeader("Accept", "application/json");
    httpGet.setHeader("Content-type", "application/json");
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
    JsonNode res =  objectMapper.readTree(responseData);
    if (!Objects.equals(res.get("code").asText(), "00")) {
      throw new PayOSError(res.get("code").asText(), res.get("desc").asText());
    }
    String paymentLinkResSignature = Utils.createSignatureFromObj(res.get("data"), checksumKey);
    if (!paymentLinkResSignature.equals(res.get("signature").asText())) {
      throw new Exception(Constants.ERROR_MESSAGE.get("DATA_NOT_INTEGRITY"));
    }
    return res.get("data");
  }
  public String confirmWebhook(String webhookUrl) throws Exception {
    if (webhookUrl == null || webhookUrl.isEmpty()){
      throw new Exception(Constants.ERROR_MESSAGE.get("INVALID_PARAMETER"));
    }
    ObjectMapper objectMapper = new ObjectMapper();
    String url = PAYOS_BASE_URL + "/confirm-webhook";
    CloseableHttpClient client = HttpClients.createDefault();
    final HttpPost httpPost = new HttpPost(url);
    httpPost.setHeader("Accept", "application/json");
    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("x-client-id", clientId);
    httpPost.setHeader("x-api-key", apiKey);
    httpPost.setEntity(new StringEntity("{\"webhookUrl\":\"" + webhookUrl + "\"}"));
    CloseableHttpResponse response = client.execute(httpPost);
    int statusCode = response.getCode();
    response.close();
    client.close();

    if(statusCode == 200){
      return webhookUrl;
    }
    else if (statusCode == 404){
      throw new PayOSError(Constants.ERROR_CODE.get("INTERNAL_SERVER_ERROR"), Constants.ERROR_MESSAGE.get("INTERNAL_SERVER_ERROR"));
    }
    else if (statusCode == 401){
      throw new PayOSError(Constants.ERROR_CODE.get("UNAUTHORIZED"), Constants.ERROR_MESSAGE.get("UNAUTHORIZED"));
    }
    throw new PayOSError(Constants.ERROR_CODE.get("INTERNAL_SERVER_ERROR"), Constants.ERROR_MESSAGE.get("INTERNAL_SERVER_ERROR"));
  }
  public JsonNode cancelPaymentLink(int orderId, String cancellationReason)
      throws Exception {
    String url = PAYOS_BASE_URL + "/v2/payment-requests/" + orderId + "/cancel";
    ObjectMapper objectMapper = new ObjectMapper();
    CloseableHttpClient client = HttpClients.createDefault();
    final HttpPost httpPost = new HttpPost(url);
    httpPost.setHeader("Accept", "application/json");
    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("x-client-id", clientId);
    httpPost.setHeader("x-api-key", apiKey);
    httpPost.setEntity(new StringEntity("{\"cancellationReason\":\"" + cancellationReason + "\"}"));
    CloseableHttpResponse response = client.execute(httpPost);
    int statusCode = response.getCode();
    if (statusCode != 200){
      throw new PayOSError(Constants.ERROR_CODE.get("INTERNAL_SERVER_ERROR"), Constants.ERROR_MESSAGE.get("INTERNAL_SERVER_ERROR"));
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
    JsonNode res =  objectMapper.readTree(responseData);
    if (!Objects.equals(res.get("code").asText(), "00")){
      throw new PayOSError(res.get("code").asText(), res.get("desc").asText());
    }
    String paymentLinkInfoResSignature = Utils.createSignatureFromObj(res.get("data"), checksumKey);
    if (!paymentLinkInfoResSignature.equals(res.get("signature").asText())) {
      throw new Exception(Constants.ERROR_MESSAGE.get("DATA_NOT_INTEGRITY"));
    }
    return res.get("data");
  }

  public JsonNode verifyPaymentWebhookData(JsonNode webhookBody) throws Exception {
    JsonNode data = webhookBody.get("data");
    String signature = webhookBody.get("signature").asText();
    if (data.isNull())
      throw new Exception(Constants.ERROR_MESSAGE.get("NO_DATA"));
    if (signature.isEmpty()){
      throw new Exception(Constants.ERROR_MESSAGE.get("NO_SIGNATURE"));
    }
    String signData = Utils.createSignatureFromObj(data, checksumKey);
    if (!signData.equals(signature)) {
      throw new Exception(Constants.ERROR_MESSAGE.get("DATA_NOT_INTEGRITY"));
    }
    return data;
  }
}
