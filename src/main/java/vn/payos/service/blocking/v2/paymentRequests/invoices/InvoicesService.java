package vn.payos.service.blocking.v2.paymentRequests.invoices;

import vn.payos.core.FileDownloadResponse;
import vn.payos.core.RequestOptions;
import vn.payos.model.v2.paymentRequests.invoices.InvoicesInfo;

/** InvoicesService */
public interface InvoicesService {
  // #region get

  /**
   * Retrieve invoices of a payment link by payment link ID.
   *
   * @param paymentLinkId Payment link ID.
   * @param options Additional options.
   * @return Invoice information.
   */
  InvoicesInfo get(String paymentLinkId, RequestOptions<Void> options);

  /**
   * Retrieve invoices of a payment link by order code.
   *
   * @param orderCode Order code.
   * @param options Additional options.
   * @return Invoice information.
   */
  InvoicesInfo get(Long orderCode, RequestOptions<Void> options);

  /**
   * Retrieve invoices of a payment link by payment link ID.
   *
   * @param paymentLinkId Payment link ID.
   * @return Invoice information.
   */
  InvoicesInfo get(String paymentLinkId);

  /**
   * Retrieve invoices of a payment link by order code.
   *
   * @param orderCode Order code.
   * @return Invoice information.
   */
  InvoicesInfo get(Long orderCode);

  // #endregion get

  // #region download

  /**
   * Download an invoice in PDF format by payment link ID.
   *
   * @param invoiceId Invoice ID.
   * @param paymentLinkId Payment link ID.
   * @param options Additional options.
   * @return Invoice file.
   */
  FileDownloadResponse download(
      String invoiceId, String paymentLinkId, RequestOptions<Void> options);

  /**
   * Download an invoice in PDF format by order code.
   *
   * @param invoiceId Invoice ID.
   * @param orderCode Order code.
   * @param options Additional options.
   * @return Invoice file.
   */
  FileDownloadResponse download(String invoiceId, Long orderCode, RequestOptions<Void> options);

  /**
   * Download an invoice in PDF format by payment link ID.
   *
   * @param invoiceId Invoice ID.
   * @param paymentLinkId Payment link ID.
   * @return Invoice file.
   */
  FileDownloadResponse download(String invoiceId, String paymentLinkId);

  /**
   * Download an invoice in PDF format by order code.
   *
   * @param invoiceId Invoice ID.
   * @param orderCode Order code.
   * @return Invoice file.
   */
  FileDownloadResponse download(String invoiceId, Long orderCode);

  // #endregion download
}
