package vn.payos.type;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

/** Transaction */
@Getter
@Setter
@Builder
@Jacksonized
public class Transaction {
  @NonNull
  private String reference;
  @NonNull
  private Integer amount;
  @NonNull
  private String accountNumber;
  @NonNull
  private String description;
  @NonNull
  private String transactionDateTime;
  private String virtualAccountName;
  private String virtualAccountNumber;
  private String counterAccountBankId;
  private String counterAccountBankName;
  private String counterAccountName;
  private String counterAccountNumber;
}
