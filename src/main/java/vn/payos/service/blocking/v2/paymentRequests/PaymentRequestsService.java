package vn.payos.service.blocking.v2.paymentRequests;

import vn.payos.core.RequestOptions;
import vn.payos.model.v2.paymentRequests.CancelPaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.service.blocking.v2.paymentRequests.invoices.InvoicesService;

/** PaymentRequestsService */
public interface PaymentRequestsService {
  // #region create

  /**
   * Creates a payment link for the provided order data.
   *
   * @param paymentData The details of payment link to be created.
   * @param options Additional options.
   * @return Create payment link detail.
   */
  CreatePaymentLinkResponse create(
      CreatePaymentLinkRequest paymentData, RequestOptions<CreatePaymentLinkRequest> options);

  /**
   * Creates a payment link for the provided order data.
   *
   * @param paymentData The details of payment link to be created.
   * @return Create payment link detail.
   */
  CreatePaymentLinkResponse create(CreatePaymentLinkRequest paymentData);

  // #endregion create

  // #region get

  /**
   * Retrieve payment link information from payment link ID.
   *
   * @param paymentLinkId The payment link ID.
   * @param options Additional options.
   * @return Payment link detail.
   */
  PaymentLink get(String paymentLinkId, RequestOptions<Void> options);

  /**
   * Retrieve payment link information from payment link ID.
   *
   * @param paymentLinkId The payment link ID.
   * @return Payment link detail.
   */
  PaymentLink get(String paymentLinkId);

  /**
   * Retrieve payment link information from order code.
   *
   * @param orderCode The order code.
   * @param options Additional options.
   * @return Payment link detail.
   */
  PaymentLink get(Long orderCode, RequestOptions<Void> options);

  /**
   * Retrieve payment link information from order code.
   *
   * @param orderCode The order code.
   * @return Payment link detail.
   */
  PaymentLink get(Long orderCode);

  // #endregion get

  // #region cancel

  /**
   * Cancel a payment link by payment link ID.
   *
   * @param paymentLinkId The payment link ID.
   * @param cancellationReason The cancellation reason.
   * @param options Additional options.
   * @return Payment link detail.
   */
  PaymentLink cancel(
      String paymentLinkId,
      String cancellationReason,
      RequestOptions<CancelPaymentLinkRequest> options);

  /**
   * Cancel a payment link by payment link ID.
   *
   * @param paymentLinkId The payment link ID.
   * @param cancellationReason The cancellation reason.
   * @return Payment link detail.
   */
  PaymentLink cancel(String paymentLinkId, String cancellationReason);

  /**
   * Cancel a payment link by payment link ID.
   *
   * @param paymentLinkId The payment link ID.
   * @return Payment link detail.
   */
  PaymentLink cancel(String paymentLinkId);

  /**
   * Cancel a payment link by order code.
   *
   * @param orderCode The order code.
   * @param cancellationReason The cancellation reason.
   * @param options Additional options.
   * @return Payment link detail.
   */
  PaymentLink cancel(
      Long orderCode, String cancellationReason, RequestOptions<CancelPaymentLinkRequest> options);

  /**
   * Cancel a payment link by order code.
   *
   * @param orderCode The order code.
   * @param cancellationReason The cancellation reason.
   * @return Payment link detail.
   */
  PaymentLink cancel(Long orderCode, String cancellationReason);

  /**
   * Cancel a payment link by order code.
   *
   * @param orderCode The order code.
   * @return Payment link detail.
   */
  PaymentLink cancel(Long orderCode);

  // #endregion cancel

  // #region invoices
  /**
   * InvoicesService
   *
   * @return InvoicesService
   */
  public InvoicesService invoices();

  // #endregion invoices
}
