package vn.payos.service.blocking.v1.payouts.batch;

import vn.payos.PayOS;
import vn.payos.core.APIService;
import vn.payos.core.RequestOptions;
import vn.payos.model.v1.payouts.Payout;
import vn.payos.model.v1.payouts.batch.PayoutBatchRequest;

/** BatchServiceImpl */
public class BatchServiceImpl extends APIService<PayOS> implements BatchService {
  /**
   * BatchServiceImpl
   *
   * @param client HTTP client
   */
  public BatchServiceImpl(PayOS client) {
    super(client);
  }

  @Override
  public Payout create(
      PayoutBatchRequest payoutData,
      String idempotencyKey,
      RequestOptions<PayoutBatchRequest> options) {
    RequestOptions<PayoutBatchRequest> requestOptions =
        RequestOptions.<PayoutBatchRequest>builder()
            .signatureOpts(
                RequestOptions.SignatureOptions.builder()
                    .request(RequestOptions.RequestSigning.HEADER)
                    .response(RequestOptions.ResponseSigning.HEADER)
                    .build())
            .build();
    if (options != null) {
      requestOptions.setQueries(options.getQueries());
      requestOptions.setHeaders(options.getHeaders());
      requestOptions.setMaxRetries(options.getMaxRetries());
      requestOptions.setTimeout(options.getTimeout());
    }

    requestOptions.setBody(payoutData);
    String randomIdempotencyKey =
        idempotencyKey == null ? this.client.getCrypto().createUuidv4() : idempotencyKey;
    requestOptions.setHeader("x-idempotency-key", randomIdempotencyKey);
    return client.post("/v1/payouts/batch", Payout.class, requestOptions);
  }

  @Override
  public Payout create(PayoutBatchRequest payoutData, String idempotencyKey) {
    return this.create(payoutData, idempotencyKey, null);
  }

  @Override
  public Payout create(PayoutBatchRequest payoutData, RequestOptions<PayoutBatchRequest> options) {
    return this.create(payoutData, null, options);
  }

  @Override
  public Payout create(PayoutBatchRequest payoutData) {
    return this.create(payoutData, null, null);
  }
}
