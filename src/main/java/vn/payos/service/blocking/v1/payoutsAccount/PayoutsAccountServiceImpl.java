package vn.payos.service.blocking.v1.payoutsAccount;

import vn.payos.PayOS;
import vn.payos.core.APIService;
import vn.payos.core.RequestOptions;
import vn.payos.model.v1.payoutsAccount.PayoutAccountInfo;

/** PayoutsAccountServiceImpl */
public class PayoutsAccountServiceImpl extends APIService<PayOS> implements PayoutsAccountService {
  /**
   * PayoutsAccountServiceImpl
   *
   * @param client client
   */
  public PayoutsAccountServiceImpl(PayOS client) {
    super(client);
  }

  @Override
  public PayoutAccountInfo balance(RequestOptions<Void> options) {
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
    return this.client.get("/v1/payouts-account/balance", PayoutAccountInfo.class, requestOptions);
  }

  @Override
  public PayoutAccountInfo balance() {
    return this.balance(null);
  }
}
