package vn.payos.service.async.v2.paymentRequests;

import java.util.concurrent.CompletableFuture;
import vn.payos.PayOSAsync;
import vn.payos.core.APIService;
import vn.payos.core.RequestOptions;
import vn.payos.model.v2.paymentRequests.CancelPaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.service.async.v2.paymentRequests.invoices.InvoicesService;
import vn.payos.service.async.v2.paymentRequests.invoices.InvoicesServiceImpl;
import vn.payos.util.SafeNumberUtils;

/** PaymentRequestsServiceImpl */
public class PaymentRequestsServiceImpl extends APIService<PayOSAsync>
    implements PaymentRequestsService {
  /**
   * PaymentRequestsServiceImpl
   *
   * @param client client
   */
  public PaymentRequestsServiceImpl(PayOSAsync client) {
    super(client);
  }

  @Override
  public CompletableFuture<CreatePaymentLinkResponse> create(
      CreatePaymentLinkRequest paymentData, RequestOptions<CreatePaymentLinkRequest> options) {
    SafeNumberUtils.validateSafeNumber(paymentData.getOrderCode(), "orderCode");
    RequestOptions<CreatePaymentLinkRequest> requestOptions =
        RequestOptions.<CreatePaymentLinkRequest>builder()
            .signatureOpts(
                RequestOptions.SignatureOptions.builder()
                    .request(RequestOptions.RequestSigning.CREATE_PAYMENT_LINK)
                    .response(RequestOptions.ResponseSigning.BODY)
                    .build())
            .build();

    if (options != null) {
      requestOptions.setQueries(options.getQueries());
      requestOptions.setHeaders(options.getHeaders());
      requestOptions.setMaxRetries(options.getMaxRetries());
      requestOptions.setTimeout(options.getTimeout());
    }

    requestOptions.setBody(paymentData);

    return client.postAsync(
        "/v2/payment-requests", CreatePaymentLinkResponse.class, requestOptions);
  }

  @Override
  public CompletableFuture<CreatePaymentLinkResponse> create(CreatePaymentLinkRequest paymentData) {
    return create(paymentData, null);
  }

  @Override
  public CompletableFuture<PaymentLink> get(String paymentLinkId, RequestOptions<Void> options) {
    RequestOptions<Void> requestOptions =
        RequestOptions.<Void>builder()
            .signatureOpts(
                RequestOptions.SignatureOptions.builder()
                    .response(RequestOptions.ResponseSigning.BODY)
                    .build())
            .build();
    if (options != null) {
      requestOptions.setQueries(options.getQueries());
      requestOptions.setHeaders(options.getHeaders());
      requestOptions.setMaxRetries(options.getMaxRetries());
      requestOptions.setTimeout(options.getTimeout());
    }

    return client.getAsync(
        "/v2/payment-requests/" + paymentLinkId, PaymentLink.class, requestOptions);
  }

  @Override
  public CompletableFuture<PaymentLink> get(String paymentLinkId) {
    return get(paymentLinkId, null);
  }

  @Override
  public CompletableFuture<PaymentLink> get(Long orderCode, RequestOptions<Void> options) {
    SafeNumberUtils.validateSafeNumber(orderCode, "orderCode");
    RequestOptions<Void> requestOptions =
        RequestOptions.<Void>builder()
            .signatureOpts(
                RequestOptions.SignatureOptions.builder()
                    .response(RequestOptions.ResponseSigning.BODY)
                    .build())
            .build();
    if (options != null) {
      requestOptions.setQueries(options.getQueries());
      requestOptions.setHeaders(options.getHeaders());
      requestOptions.setMaxRetries(options.getMaxRetries());
      requestOptions.setTimeout(options.getTimeout());
    }

    return client.getAsync("/v2/payment-requests/" + orderCode, PaymentLink.class, requestOptions);
  }

  @Override
  public CompletableFuture<PaymentLink> get(Long orderCode) {
    return get(orderCode, null);
  }

  @Override
  public CompletableFuture<PaymentLink> cancel(
      String paymentLinkId,
      String cancellationReason,
      RequestOptions<CancelPaymentLinkRequest> options) {
    CancelPaymentLinkRequest requestData = null;
    if (cancellationReason != null && !cancellationReason.isEmpty()) {
      requestData =
          CancelPaymentLinkRequest.builder().cancellationReason(cancellationReason).build();
    }

    RequestOptions<CancelPaymentLinkRequest> requestOptions =
        RequestOptions.<CancelPaymentLinkRequest>builder()
            .signatureOpts(
                RequestOptions.SignatureOptions.builder()
                    .response(RequestOptions.ResponseSigning.BODY)
                    .build())
            .build();

    if (options != null) {
      requestOptions.setQueries(options.getQueries());
      requestOptions.setHeaders(options.getHeaders());
      requestOptions.setMaxRetries(options.getMaxRetries());
      requestOptions.setTimeout(options.getTimeout());
    }

    requestOptions.setBody(requestData);

    return client.postAsync(
        "/v2/payment-requests/" + paymentLinkId + "/cancel", PaymentLink.class, requestOptions);
  }

  @Override
  public CompletableFuture<PaymentLink> cancel(String paymentLinkId, String cancellationReason) {
    return cancel(paymentLinkId, cancellationReason, null);
  }

  @Override
  public CompletableFuture<PaymentLink> cancel(String paymentLinkId) {
    return cancel(paymentLinkId, null, null);
  }

  @Override
  public CompletableFuture<PaymentLink> cancel(
      Long orderCode, String cancellationReason, RequestOptions<CancelPaymentLinkRequest> options) {
    SafeNumberUtils.validateSafeNumber(orderCode, "orderCode");
    CancelPaymentLinkRequest requestData = null;
    if (cancellationReason != null && !cancellationReason.isEmpty()) {
      requestData =
          CancelPaymentLinkRequest.builder().cancellationReason(cancellationReason).build();
    }

    RequestOptions<CancelPaymentLinkRequest> requestOptions =
        RequestOptions.<CancelPaymentLinkRequest>builder()
            .signatureOpts(
                RequestOptions.SignatureOptions.builder()
                    .response(RequestOptions.ResponseSigning.BODY)
                    .build())
            .build();

    if (options != null) {
      requestOptions.setQueries(options.getQueries());
      requestOptions.setHeaders(options.getHeaders());
      requestOptions.setMaxRetries(options.getMaxRetries());
      requestOptions.setTimeout(options.getTimeout());
    }

    requestOptions.setBody(requestData);

    return client.postAsync(
        "/v2/payment-requests/" + orderCode + "/cancel", PaymentLink.class, requestOptions);
  }

  @Override
  public CompletableFuture<PaymentLink> cancel(Long orderCode, String cancellationReason) {
    return cancel(orderCode, cancellationReason, null);
  }

  @Override
  public CompletableFuture<PaymentLink> cancel(Long orderCode) {
    return cancel(orderCode, null, null);
  }

  @Override
  public InvoicesService invoices() {
    return new InvoicesServiceImpl(client);
  }
}
