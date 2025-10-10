package vn.payos.model.v1.payouts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** PayoutTransaction */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutTransaction {
  @NonNull
  @JsonProperty("id")
  private String id;

  @NonNull
  @JsonProperty("referenceId")
  private String referenceId;

  @NonNull
  @JsonProperty("amount")
  private Long amount;

  @NonNull
  @JsonProperty("description")
  private String description;

  @NonNull
  @JsonProperty("toBin")
  private String toBin;

  @NonNull
  @JsonProperty("toAccountNumber")
  private String toAccountNumber;

  @JsonProperty("toAccountName")
  private String toAccountName;

  @JsonProperty("reference")
  private String reference;

  @JsonProperty("transactionDatetime")
  private String transactionDatetime;

  @JsonProperty("errorMessage")
  private String errorMessage;

  @JsonProperty("errorCode")
  private String errorCode;

  @NonNull
  @JsonProperty("state")
  private PayoutTransactionState state;
}
