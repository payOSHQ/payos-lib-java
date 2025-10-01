package vn.payos.service.blocking.v1.payouts;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import vn.payos.PayOS;
import vn.payos.core.APIService;
import vn.payos.core.FinalRequestOptions;
import vn.payos.core.Page;
import vn.payos.core.RequestOptions;
import vn.payos.model.v1.payouts.EstimateCredit;
import vn.payos.model.v1.payouts.GetPayoutListParams;
import vn.payos.model.v1.payouts.Payout;
import vn.payos.model.v1.payouts.PayoutListResponse;
import vn.payos.model.v1.payouts.PayoutRequests;
import vn.payos.model.v1.payouts.batch.PayoutBatchRequest;
import vn.payos.service.blocking.v1.payouts.batch.BatchService;
import vn.payos.service.blocking.v1.payouts.batch.BatchServiceImpl;

/** PayoutsServiceImpl */
public class PayoutsServiceImpl extends APIService<PayOS> implements PayoutsService {
  /**
   * PayoutsServiceImpl
   *
   * @param client client
   */
  public PayoutsServiceImpl(PayOS client) {
    super(client);
  }

  @Override
  public Payout create(
      PayoutRequests payoutData, String idempotencyKey, RequestOptions<PayoutRequests> options) {
    RequestOptions<PayoutRequests> requestOptions =
        RequestOptions.<PayoutRequests>builder()
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
    return client.post("/v1/payouts", Payout.class, requestOptions);
  }

  @Override
  public Payout create(PayoutRequests payoutData, RequestOptions<PayoutRequests> options) {
    return this.create(payoutData, null, options);
  }

  @Override
  public Payout create(PayoutRequests payoutData, String idempotencyKey) {
    return this.create(payoutData, idempotencyKey, null);
  }

  @Override
  public Payout create(PayoutRequests payoutData) {
    return this.create(payoutData, null, null);
  }

  @Override
  public Payout get(String payoutId, RequestOptions<Void> options) {
    RequestOptions<Void> requestOptions =
        RequestOptions.<Void>builder()
            .signatureOpts(
                RequestOptions.SignatureOptions.builder()
                    .response(RequestOptions.ResponseSigning.HEADER)
                    .build())
            .build();
    if (options != null) {
      requestOptions.setQueries(options.getQueries());
      requestOptions.setHeaders(options.getHeaders());
      requestOptions.setMaxRetries(options.getMaxRetries());
      requestOptions.setTimeout(options.getTimeout());
    }
    return this.client.get("/v1/payouts/" + payoutId, Payout.class, requestOptions);
  }

  @Override
  public Payout get(String payoutId) {
    return this.get(payoutId, null);
  }

  @Override
  public EstimateCredit estimateCredit(
      PayoutRequests payoutData, RequestOptions<PayoutRequests> options) {
    RequestOptions<PayoutRequests> requestOptions =
        RequestOptions.<PayoutRequests>builder()
            .signatureOpts(
                RequestOptions.SignatureOptions.builder()
                    .request(RequestOptions.RequestSigning.HEADER)
                    .build())
            .build();
    if (options != null) {
      requestOptions.setQueries(options.getQueries());
      requestOptions.setHeaders(options.getHeaders());
      requestOptions.setMaxRetries(options.getMaxRetries());
      requestOptions.setTimeout(options.getTimeout());
    }
    return this.client.post("/v1/payouts/estimate-credit", EstimateCredit.class, requestOptions);
  }

  @Override
  public EstimateCredit estimateCredit(PayoutRequests payoutData) {
    return this.estimateCredit(payoutData, null);
  }

  @Override
  public EstimateCredit estimateCredit(
      PayoutBatchRequest payoutData, RequestOptions<PayoutBatchRequest> options) {
    RequestOptions<PayoutBatchRequest> requestOptions =
        RequestOptions.<PayoutBatchRequest>builder()
            .signatureOpts(
                RequestOptions.SignatureOptions.builder()
                    .request(RequestOptions.RequestSigning.HEADER)
                    .build())
            .build();
    if (options != null) {
      requestOptions.setQueries(options.getQueries());
      requestOptions.setHeaders(options.getHeaders());
      requestOptions.setMaxRetries(options.getMaxRetries());
      requestOptions.setTimeout(options.getTimeout());
    }
    return this.client.post("/v1/payouts/estimate-credit", EstimateCredit.class, requestOptions);
  }

  @Override
  public EstimateCredit estimateCredit(PayoutBatchRequest payoutData) {
    return this.estimateCredit(payoutData, null);
  }

  @Override
  public Page<Payout> list(GetPayoutListParams params, RequestOptions<Void> options) {
    RequestOptions<Void> requestOptions =
        RequestOptions.<Void>builder()
            .signatureOpts(
                RequestOptions.SignatureOptions.builder()
                    .response(RequestOptions.ResponseSigning.HEADER)
                    .build())
            .build();

    if (options != null) {
      requestOptions.setQueries(options.getQueries());
      requestOptions.setHeaders(options.getHeaders());
      requestOptions.setMaxRetries(options.getMaxRetries());
      requestOptions.setTimeout(options.getTimeout());
    }

    Map<String, Object> queryParams = new HashMap<>(requestOptions.getQueries());
    if (params != null) {
      if (params.getReferenceId() != null) {
        queryParams.put("referenceId", params.getReferenceId());
      }
      if (params.getApprovalState() != null) {
        queryParams.put("approvalState", params.getApprovalState().getValue());
      }
      if (params.getCategory() != null && !params.getCategory().isEmpty()) {
        queryParams.put("category", String.join(",", params.getCategory()));
      }
      if (params.getFromDate() != null) {
        queryParams.put(
            "fromDate", params.getFromDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
      }
      if (params.getToDate() != null) {
        queryParams.put(
            "toDate", params.getToDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
      }
      if (params.getLimit() != null) {
        queryParams.put("limit", params.getLimit());
      }
      if (params.getOffset() != null) {
        queryParams.put("offset", params.getOffset());
      }
    }

    requestOptions.setQueries(queryParams);

    FinalRequestOptions<?> finalOptions =
        FinalRequestOptions.fromRequestOptions(
            FinalRequestOptions.HTTPMethod.GET, "/v1/payouts", requestOptions);

    PayoutListResponse response = client.request(finalOptions, PayoutListResponse.class);

    return new Page<Payout>(client, response, PayoutListResponse.class, finalOptions);
  }

  @Override
  public Page<Payout> list(GetPayoutListParams params) {
    return this.list(params, null);
  }

  @Override
  public BatchService batch() {
    return new BatchServiceImpl(this.client);
  }
}
