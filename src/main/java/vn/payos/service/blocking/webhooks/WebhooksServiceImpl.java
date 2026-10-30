package vn.payos.service.blocking.webhooks;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import vn.payos.PayOS;
import vn.payos.core.APIService;
import vn.payos.core.RequestOptions;
import vn.payos.exception.WebhookException;
import vn.payos.model.webhooks.ConfirmWebhookRequest;
import vn.payos.model.webhooks.ConfirmWebhookResponse;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;
import vn.payos.util.DataConverterUtils;
import vn.payos.util.WebhookVerification;

/** WebhooksServiceImpl */
public class WebhooksServiceImpl extends APIService<PayOS> implements WebhooksService {
  /**
   * WebhooksServiceImpl
   *
   * @param client client
   */
  public WebhooksServiceImpl(PayOS client) {
    super(client);
  }

  @Override
  public ConfirmWebhookResponse confirm(
      ConfirmWebhookRequest webhookConfirmRequest, RequestOptions<ConfirmWebhookRequest> options) {
    RequestOptions<ConfirmWebhookRequest> requestOptions =
        RequestOptions.<ConfirmWebhookRequest>builder().build();

    if (options != null) {
      requestOptions.setQueries(options.getQueries());
      requestOptions.setHeaders(options.getHeaders());
      requestOptions.setMaxRetries(options.getMaxRetries());
      requestOptions.setTimeout(options.getTimeout());
    }

    requestOptions.setBody(webhookConfirmRequest);

    return this.client.post("/confirm-webhook", ConfirmWebhookResponse.class, requestOptions);
  }

  @Override
  public ConfirmWebhookResponse confirm(
      String webhookUrl, RequestOptions<ConfirmWebhookRequest> options) {
    ConfirmWebhookRequest webhookConfirmRequest = new ConfirmWebhookRequest(webhookUrl);
    return this.confirm(webhookConfirmRequest, options);
  }

  @Override
  public ConfirmWebhookResponse confirm(ConfirmWebhookRequest webhookConfirmRequest) {
    return this.confirm(webhookConfirmRequest, null);
  }

  @Override
  public ConfirmWebhookResponse confirm(String webhookUrl) {
    ConfirmWebhookRequest webhookConfirmRequest = new ConfirmWebhookRequest(webhookUrl);
    return this.confirm(webhookConfirmRequest, null);
  }

  @Override
  public WebhookData verify(Object webhook) {
    String checksumKey = this.client.getOptions().getChecksumKey();
    if (webhook instanceof Webhook) {
      Webhook typedWebhook = (Webhook) webhook;
      WebhookData webhookData = typedWebhook.getData();
      String signature = typedWebhook.getSignature();
      if (signature == null || signature.isEmpty()) {
        throw new WebhookException("Invalid signature");
      }
      WebhookVerification.validateTyped(webhookData);
      String signedSignature =
          this.client.getCrypto().createSignatureFromObj(webhookData, checksumKey);
      if (signedSignature.isEmpty()
          || !WebhookVerification.constantTimeEquals(signedSignature, signature)) {
        throw new WebhookException("Data not integrity");
      }
      return webhookData;
    }

    JsonNode root = WebhookVerification.toTree(webhook);
    if (root == null || !root.isObject()) {
      throw new WebhookException("Invalid webhook data");
    }
    JsonNode data = root.get("data");
    if (data == null || data.isNull()) {
      throw new WebhookException("Invalid webhook data");
    }
    WebhookVerification.validateData(data);
    JsonNode sig = root.get("signature");
    if (sig == null || !sig.isTextual() || sig.asText().isEmpty()) {
      throw new WebhookException("Invalid signature");
    }
    Map<String, Object> wire =
        new ObjectMapper().convertValue(data, new TypeReference<Map<String, Object>>() {});
    String signed = this.client.getCrypto().createSignatureFromObj(wire, checksumKey);
    if (signed.isEmpty() || !WebhookVerification.constantTimeEquals(signed, sig.asText())) {
      throw new WebhookException("Data not integrity");
    }
    return DataConverterUtils.normalize(data, WebhookData.class);
  }
}
