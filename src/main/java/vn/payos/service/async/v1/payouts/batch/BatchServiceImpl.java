package vn.payos.service.async.v1.payouts.batch;

import java.util.concurrent.CompletableFuture;
import vn.payos.PayOSAsync;
import vn.payos.core.APIService;
import vn.payos.core.RequestOptions;
import vn.payos.model.v1.payouts.Payout;
import vn.payos.model.v1.payouts.batch.PayoutBatchRequest;

/** BatchServiceImpl */
public class BatchServiceImpl extends APIService<PayOSAsync> implements BatchService {
  /**
   * BatchServiceImpl
   *
   * @param client HTTP client
   */
  public BatchServiceImpl(PayOSAsync client) {
    super(client);
  }

  @Override
  public CompletableFuture<Payout> create(
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
    return client.postAsync("/v1/payouts/batch", Payout.class, requestOptions);
  }

  @Override
  public CompletableFuture<Payout> create(PayoutBatchRequest payoutData, String idempotencyKey) {
    return this.create(payoutData, idempotencyKey, null);
  }

  @Override
  public CompletableFuture<Payout> create(
      PayoutBatchRequest payoutData, RequestOptions<PayoutBatchRequest> options) {
    return this.create(payoutData, null, options);
  }

  @Override
  public CompletableFuture<Payout> create(PayoutBatchRequest payoutData) {
    return this.create(payoutData, null, null);
  }
}
