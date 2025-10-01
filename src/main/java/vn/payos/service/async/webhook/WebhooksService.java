package vn.payos.service.async.webhook;

import java.util.concurrent.CompletableFuture;
import vn.payos.core.RequestOptions;
import vn.payos.model.webhooks.ConfirmWebhookRequest;
import vn.payos.model.webhooks.ConfirmWebhookResponse;
import vn.payos.model.webhooks.WebhookData;

/** WebhooksService */
public interface WebhooksService {
  /**
   * Validate and register a webhook URL with payOS. payOS will test the webhook endpoint by sending
   * a validation request to it. If the webhook responds correctly, it will be registered for
   * payment notifications.
   *
   * @param webhookUrl Your webhook URL endpoint that will receive payment notifications.
   * @param options Additional options.
   * @return Confirm result.
   */
  CompletableFuture<ConfirmWebhookResponse> confirm(
      String webhookUrl, RequestOptions<ConfirmWebhookRequest> options);

  /**
   * Validate and register a webhook URL with payOS. payOS will test the webhook endpoint by sending
   * a validation request to it. If the webhook responds correctly, it will be registered for
   * payment notifications.
   *
   * @param webhookUrl Your webhook URL endpoint that will receive payment notifications.
   * @return Confirm result.
   */
  CompletableFuture<ConfirmWebhookResponse> confirm(String webhookUrl);

  /**
   * Validate and register a webhook URL with payOS. payOS will test the webhook endpoint by sending
   * a validation request to it. If the webhook responds correctly, it will be registered for
   * payment notifications.
   *
   * @param webhookConfirmRequest Object containing your webhook URL endpoint that will receive
   *     payment notifications.
   * @param options Additional options.
   * @return Confirm result.
   */
  CompletableFuture<ConfirmWebhookResponse> confirm(
      ConfirmWebhookRequest webhookConfirmRequest, RequestOptions<ConfirmWebhookRequest> options);

  /**
   * Validate and register a webhook URL with payOS. payOS will test the webhook endpoint by sending
   * a validation request to it. If the webhook responds correctly, it will be registered for
   * payment notifications.
   *
   * @param webhookConfirmRequest Object containing your webhook URL endpoint that will receive
   *     payment notifications.
   * @return Confirm result.
   */
  CompletableFuture<ConfirmWebhookResponse> confirm(ConfirmWebhookRequest webhookConfirmRequest);

  /**
   * Verify the webhook data sent from payOS.
   *
   * @param webhook The request body receive from payOS.
   * @return The verified webhook data.
   */
  CompletableFuture<WebhookData> verify(Object webhook);
}
