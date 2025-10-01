package vn.payos.service.blocking.v1.payouts;

import vn.payos.core.Page;
import vn.payos.core.RequestOptions;
import vn.payos.model.v1.payouts.EstimateCredit;
import vn.payos.model.v1.payouts.GetPayoutListParams;
import vn.payos.model.v1.payouts.Payout;
import vn.payos.model.v1.payouts.PayoutRequests;
import vn.payos.model.v1.payouts.batch.PayoutBatchRequest;
import vn.payos.service.blocking.v1.payouts.batch.BatchService;

/** PayoutsService */
public interface PayoutsService {
  // #region create
  /**
   * Create a new payout.
   *
   * @param payoutData The details of the payout to be created.
   * @param idempotencyKey A unique key for ensuring idempotency. Use a UUID or other high-entropy
   *     string so that retries of the same request are recognized and duplicated payouts are
   *     prevented.
   * @param options Additional options.
   * @return Payout detail.
   */
  Payout create(
      PayoutRequests payoutData, String idempotencyKey, RequestOptions<PayoutRequests> options);

  /**
   * Create a new payout.
   *
   * @param payoutData The details of the payout to be created.
   * @param options Additional options.
   * @return Payout detail.
   */
  Payout create(PayoutRequests payoutData, RequestOptions<PayoutRequests> options);

  /**
   * Create a new payout.
   *
   * @param payoutData The details of the payout to be created.
   * @param idempotencyKey A unique key for ensuring idempotency. Use a UUID or other high-entropy
   *     string so that retries of the same request are recognized and duplicated payouts are
   *     prevented.
   * @return Payout detail.
   */
  Payout create(PayoutRequests payoutData, String idempotencyKey);

  /**
   * Create a new payout.
   *
   * @param payoutData The details of the payout to be created.
   * @return Payout detail.
   */
  Payout create(PayoutRequests payoutData);

  // #endregion create

  // #region get

  /**
   * Retrieves detailed information about a specific payout.
   *
   * @param payoutId The unique identifier of the payout to retrieve.
   * @param options Additional options.
   * @return Payout detail.
   */
  Payout get(String payoutId, RequestOptions<Void> options);

  /**
   * Retrieves detailed information about a specific payout.
   *
   * @param payoutId The unique identifier of the payout to retrieve.
   * @return Payout detail.
   */
  Payout get(String payoutId);

  // #endregion get

  // #region estimateCredit

  /**
   * Estimate credit required for one payout.
   *
   * @param payoutData The details of the payout to be estimated.
   * @param options Additional options.
   * @return The estimate result.
   */
  EstimateCredit estimateCredit(PayoutRequests payoutData, RequestOptions<PayoutRequests> options);

  /**
   * Estimate credit required for one payout.
   *
   * @param payoutData The details of the payout to be estimated.
   * @return The estimate result.
   */
  EstimateCredit estimateCredit(PayoutRequests payoutData);

  /**
   * Estimate credit required for batch payout.
   *
   * @param payoutData The details of the payout to be estimated.
   * @param options Additional options.
   * @return The estimate result.
   */
  EstimateCredit estimateCredit(
      PayoutBatchRequest payoutData, RequestOptions<PayoutBatchRequest> options);

  /**
   * Estimate credit required for batch payout.
   *
   * @param payoutData The details of the payout to be estimated.
   * @return The estimate result.
   */
  EstimateCredit estimateCredit(PayoutBatchRequest payoutData);

  // #endregion estimateCredit

  // #region list

  /**
   * Retrieves a paginated list of payouts filtered by the given criteria.
   *
   * @param params The filtering options including pagination parameters.
   * @param options Additional options.
   * @return Payouts detail.
   */
  Page<Payout> list(GetPayoutListParams params, RequestOptions<Void> options);

  /**
   * Retrieves a paginated list of payouts filtered by the given criteria.
   *
   * @param params The filtering options including pagination parameters.
   * @return Payouts detail.
   */
  Page<Payout> list(GetPayoutListParams params);

  // #endregion list

  // #region batch

  /**
   * BatchService
   *
   * @return BatchService
   */
  public BatchService batch();

  // #endregion batch
}
