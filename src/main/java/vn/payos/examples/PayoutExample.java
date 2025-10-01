package vn.payos.examples;

import java.util.Arrays;
import vn.payos.PayOS;
import vn.payos.core.Page;
import vn.payos.exception.APIException;
import vn.payos.model.v1.payouts.GetPayoutListParams;
import vn.payos.model.v1.payouts.Payout;
import vn.payos.model.v1.payouts.PayoutApprovalState;
import vn.payos.model.v1.payouts.PayoutTransaction;
import vn.payos.model.v1.payouts.batch.PayoutBatchItem;
import vn.payos.model.v1.payouts.batch.PayoutBatchRequest;

public class PayoutExample {
  public static void main(String[] args) {
    PayOS client = new PayOS("PAYOS_CLIENT_ID", "PAYOS_API_KEY", "PAYOS_CHECKSUM_KEY");
    String referenceId = "payout_" + System.currentTimeMillis();
    PayoutBatchRequest payoutBatchRequest =
        PayoutBatchRequest.builder()
            .referenceId(referenceId)
            .validateDestination(false)
            .category(Arrays.asList("salary", "payment"))
            .payout(
                PayoutBatchItem.builder()
                    .referenceId(referenceId + "_1")
                    .amount(10000L)
                    .description("payment")
                    .toBin("970422")
                    .toAccountNumber("0123456789")
                    .build())
            .payout(
                PayoutBatchItem.builder()
                    .referenceId(referenceId + "_2")
                    .amount(50000L)
                    .description("another payment")
                    .toBin("970418")
                    .toAccountNumber("0123456789")
                    .build())
            .build();
    try {
      Payout payout = client.payouts().batch().create(payoutBatchRequest);
      System.out.println("ReferenceId: " + payout.getReferenceId());
      System.out.println("Approval state: " + payout.getApprovalState());
      Page<Payout> page =
          client
              .payouts()
              .list(
                  GetPayoutListParams.builder()
                      .approvalState(PayoutApprovalState.COMPLETED)
                      .build());
      page.autoPager()
          .forEach(
              item -> {
                System.out.println("ReferenceId: " + item.getReferenceId());
                for (PayoutTransaction txn : item.getTransactions()) {
                  System.out.println("Transaction referenceId: " + item.getReferenceId());
                  System.out.println("State: " + txn.getState());
                  System.out.println("Transaction datetime: " + txn.getTransactionDatetime());
                }
              });
    } catch (APIException e) {
      System.out.println(e.getErrorDesc().get());
    }
  }
}
