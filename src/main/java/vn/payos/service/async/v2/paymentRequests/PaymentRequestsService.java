package vn.payos.service.async.v2.paymentRequests;

import java.util.concurrent.CompletableFuture;
import vn.payos.core.RequestOptions;
import vn.payos.model.v2.paymentRequests.CancelPaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.service.async.v2.paymentRequests.invoices.InvoicesService;

/** PaymentRequestsService */
public interface PaymentRequestsService {
  /**
   * Creates a payment link for the provided order data asynchronously.
   *
   * @param paymentData The details of payment link to be created.
   * @param options Additional options.
   * @return Create payment link detail.
   */
  CompletableFuture<CreatePaymentLinkResponse> create(
      CreatePaymentLinkRequest paymentData, RequestOptions<CreatePaymentLinkRequest> options);

  /**
   * Creates a payment link for the provided order data asynchronously.
   *
   * @param paymentData The details of payment link to be created.
   * @return Create payment link detail.
   */
  CompletableFuture<CreatePaymentLinkResponse> create(CreatePaymentLinkRequest paymentData);

  /**
   * Retrieve payment link information from payment link ID asynchronously.
   *
   * @param paymentLinkId The payment link ID.
   * @param options Additional options.
   * @return Payment link detail.
   */
  CompletableFuture<PaymentLink> get(String paymentLinkId, RequestOptions<Void> options);

  /**
   * Retrieve payment link information from payment link ID asynchronously.
   *
   * @param paymentLinkId The payment link ID.
   * @return Payment link detail.
   */
  CompletableFuture<PaymentLink> get(String paymentLinkId);

  /**
   * Retrieve payment link information from order code asynchronously.
   *
   * @param orderCode The order code.
   * @param options Additional options.
   * @return Payment link detail.
   */
  CompletableFuture<PaymentLink> get(Long orderCode, RequestOptions<Void> options);

  /**
   * Retrieve payment link information from order code asynchronously.
   *
   * @param orderCode The order code.
   * @return Payment link detail.
   */
  CompletableFuture<PaymentLink> get(Long orderCode);

  /**
   * Cancel a payment link by payment link ID asynchronously.
   *
   * @param paymentLinkId The payment link ID.
   * @param cancellationReason The cancellation reason.
   * @param options Additional options.
   * @return Payment link detail.
   */
  CompletableFuture<PaymentLink> cancel(
      String paymentLinkId,
      String cancellationReason,
      RequestOptions<CancelPaymentLinkRequest> options);

  /**
   * Cancel a payment link by payment link ID asynchronously.
   *
   * @param paymentLinkId The payment link ID.
   * @param cancellationReason The cancellation reason.
   * @return Payment link detail.
   */
  CompletableFuture<PaymentLink> cancel(String paymentLinkId, String cancellationReason);

  /**
   * Cancel a payment link by payment link ID asynchronously.
   *
   * @param paymentLinkId The payment link ID.
   * @return Payment link detail.
   */
  CompletableFuture<PaymentLink> cancel(String paymentLinkId);

  /**
   * Cancel a payment link by order code asynchronously.
   *
   * @param orderCode The order code.
   * @param cancellationReason The cancellation reason.
   * @param options Additional options.
   * @return Payment link detail.
   */
  CompletableFuture<PaymentLink> cancel(
      Long orderCode, String cancellationReason, RequestOptions<CancelPaymentLinkRequest> options);

  /**
   * Cancel a payment link by order code asynchronously.
   *
   * @param orderCode The order code.
   * @param cancellationReason The cancellation reason.
   * @return Payment link detail.
   */
  CompletableFuture<PaymentLink> cancel(Long orderCode, String cancellationReason);

  /**
   * Cancel a payment link by order code asynchronously.
   *
   * @param orderCode The order code.
   * @return Payment link detail.
   */
  CompletableFuture<PaymentLink> cancel(Long orderCode);

  /**
   * InvoicesService
   *
   * @return InvoicesService
   */
  public InvoicesService invoices();
}
