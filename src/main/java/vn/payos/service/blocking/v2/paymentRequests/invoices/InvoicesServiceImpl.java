package vn.payos.service.blocking.v2.paymentRequests.invoices;

import vn.payos.PayOS;
import vn.payos.core.APIService;
import vn.payos.core.FileDownloadResponse;
import vn.payos.core.FinalRequestOptions;
import vn.payos.core.FinalRequestOptions.HTTPMethod;
import vn.payos.core.RequestOptions;
import vn.payos.model.v2.paymentRequests.invoices.InvoicesInfo;
import vn.payos.util.SafeNumberUtils;

/** InvoicesServiceImpl */
public class InvoicesServiceImpl extends APIService<PayOS> implements InvoicesService {
  /**
   * InvoicesServiceImpl
   *
   * @param client client
   */
  public InvoicesServiceImpl(PayOS client) {
    super(client);
  }

  @Override
  public InvoicesInfo get(String paymentLinkId, RequestOptions<Void> options) {
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

    return client.get(
        "/v2/payment-requests/" + paymentLinkId + "/invoices", InvoicesInfo.class, requestOptions);
  }

  @Override
  public InvoicesInfo get(Long orderCode, RequestOptions<Void> options) {
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

    return client.get(
        "/v2/payment-requests/" + orderCode + "/invoices", InvoicesInfo.class, requestOptions);
  }

  @Override
  public InvoicesInfo get(String paymentLinkId) {
    return this.get(paymentLinkId, null);
  }

  @Override
  public InvoicesInfo get(Long orderCode) {
    return this.get(orderCode, null);
  }

  @Override
  public FileDownloadResponse download(
      String invoiceId, String paymentLinkId, RequestOptions<Void> options) {
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

    return this.client.downloadFile(
        FinalRequestOptions.fromRequestOptions(
            HTTPMethod.GET,
            "/v2/payment-requests/" + paymentLinkId + "/invoices/" + invoiceId + "/download",
            requestOptions));
  }

  @Override
  public FileDownloadResponse download(
      String invoiceId, Long orderCode, RequestOptions<Void> options) {
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

    return this.client.downloadFile(
        FinalRequestOptions.fromRequestOptions(
            HTTPMethod.GET,
            "/v2/payment-requests/" + orderCode + "/invoices/" + invoiceId + "/download",
            requestOptions));
  }

  @Override
  public FileDownloadResponse download(String invoiceId, String paymentLinkId) {
    return this.download(invoiceId, paymentLinkId, null);
  }

  @Override
  public FileDownloadResponse download(String invoiceId, Long orderCode) {
    return this.download(invoiceId, orderCode, null);
  }
}
