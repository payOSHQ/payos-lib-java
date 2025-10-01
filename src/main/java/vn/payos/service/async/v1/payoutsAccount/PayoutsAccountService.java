package vn.payos.service.async.v1.payoutsAccount;

import java.util.concurrent.CompletableFuture;
import vn.payos.core.RequestOptions;
import vn.payos.model.v1.payoutsAccount.PayoutAccountInfo;

/** PayoutsAccountService */
public interface PayoutsAccountService {
  /**
   * Retrieves the current payout account balance
   *
   * @param options Additional options.
   * @return Balance information.
   */
  CompletableFuture<PayoutAccountInfo> balance(RequestOptions<Void> options);

  /**
   * Retrieves the current payout account balance
   *
   * @return Balance information.
   */
  CompletableFuture<PayoutAccountInfo> balance();
}
