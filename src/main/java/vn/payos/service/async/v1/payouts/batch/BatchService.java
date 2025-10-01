package vn.payos.service.async.v1.payouts.batch;

import java.util.concurrent.CompletableFuture;
import vn.payos.core.RequestOptions;
import vn.payos.model.v1.payouts.Payout;
import vn.payos.model.v1.payouts.batch.PayoutBatchRequest;

/** BatchService */
public interface BatchService {
  // #region create

  /**
   * Create a batch payout.
   *
   * @param payoutData The details of batch payout to be created.
   * @param idempotencyKey A unique key to ensure idempotency.
   * @param options Additional options.
   * @return Payout detail.
   */
  CompletableFuture<Payout> create(
      PayoutBatchRequest payoutData,
      String idempotencyKey,
      RequestOptions<PayoutBatchRequest> options);

  /**
   * Create a batch payout.
   *
   * @param payoutData The details of batch payout to be created.
   * @param idempotencyKey A unique key to ensure idempotency.
   * @return Payout detail.
   */
  CompletableFuture<Payout> create(PayoutBatchRequest payoutData, String idempotencyKey);

  /**
   * Create a batch payout.
   *
   * @param payoutData The details of batch payout to be created.
   * @param options Additional options.
   * @return Payout detail.
   */
  CompletableFuture<Payout> create(
      PayoutBatchRequest payoutData, RequestOptions<PayoutBatchRequest> options);

  /**
   * Create a batch payout.
   *
   * @param payoutData The details of batch payout to be created.
   * @return Payout detail.
   */
  CompletableFuture<Payout> create(PayoutBatchRequest payoutData);

  // #endregion create
}
