package vn.payos.service.async.v1.payoutsAccount;

import java.util.concurrent.CompletableFuture;
import vn.payos.PayOSAsync;
import vn.payos.core.APIService;
import vn.payos.core.RequestOptions;
import vn.payos.model.v1.payoutsAccount.PayoutAccountInfo;

/** PayoutsAccountServiceImpl */
public class PayoutsAccountServiceImpl extends APIService<PayOSAsync>
    implements PayoutsAccountService {
  /**
   * PayoutsAccountServiceImpl
   *
   * @param client client
   */
  public PayoutsAccountServiceImpl(PayOSAsync client) {
    super(client);
  }

  @Override
  public CompletableFuture<PayoutAccountInfo> balance(RequestOptions<Void> options) {
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
    return this.client.getAsync(
        "/v1/payouts-account/balance", PayoutAccountInfo.class, requestOptions);
  }

  @Override
  public CompletableFuture<PayoutAccountInfo> balance() {
    return this.balance(null);
  }
}
