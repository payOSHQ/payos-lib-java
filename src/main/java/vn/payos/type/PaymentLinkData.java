package vn.payos.type;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

/** Payment link data */
@Getter
@Setter
@Builder
@Jacksonized
public class PaymentLinkData {
  @NonNull
  private String id;
  @NonNull
  private Long orderCode;
  @NonNull
  private Integer amount;
  @NonNull
  private Integer amountPaid;
  @NonNull
  private Integer amountRemaining;
  @NonNull
  private String status;
  @NonNull
  private String createdAt;
  @NonNull
  private List<Transaction> transactions;
  private String cancellationReason;
  private String canceledAt;

  public void addTransaction(Transaction transaction) {
    if (this.transactions == null) {
      this.transactions = new ArrayList<>();
    }
    this.transactions.add(transaction);
  }

  public static class PaymentLinkDataBuilder {
    private List<Transaction> transactions = new ArrayList<>();

    public PaymentLinkDataBuilder transaction(Transaction transaction) {
      this.transactions.add(transaction);
      return this;
    }
  }
}
