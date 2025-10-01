package vn.payos.service.blocking.webhooks;

import vn.payos.PayOS;
import vn.payos.core.APIService;
import vn.payos.core.RequestOptions;
import vn.payos.exception.WebhookException;
import vn.payos.model.webhooks.ConfirmWebhookRequest;
import vn.payos.model.webhooks.ConfirmWebhookResponse;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;
import vn.payos.util.DataConverterUtils;

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
    Webhook normalizeWebhook = this.normalize(webhook);
    WebhookData webhookData = normalizeWebhook.getData();
    String signature = normalizeWebhook.getSignature();
    if (signature.isEmpty()) {
      throw new WebhookException("Invalid signature");
    }
    String signedSignature =
        this.client
            .getCrypto()
            .createSignatureFromObj(webhookData, this.client.getOptions().getChecksumKey());
    if (signedSignature.isEmpty() || !signedSignature.equals(signature)) {
      throw new WebhookException("Data not integrity");
    }
    return webhookData;
  }

  private Webhook normalize(Object webhookObj) {
    try {
      return DataConverterUtils.normalize(webhookObj, Webhook.class);
    } catch (IllegalArgumentException e) {
      throw new WebhookException(e.getMessage(), e);
    }
  }
}
