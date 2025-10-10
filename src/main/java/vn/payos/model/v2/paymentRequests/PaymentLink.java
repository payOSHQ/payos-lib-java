package vn.payos.model.v2.paymentRequests;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** PaymentLink */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLink {
  @NonNull
  @JsonProperty("id")
  private String id;

  @NonNull
  @JsonProperty("orderCode")
  private Long orderCode;

  @NonNull
  @JsonProperty("amount")
  private Long amount;

  @NonNull
  @JsonProperty("amountPaid")
  private Long amountPaid;

  @NonNull
  @JsonProperty("amountRemaining")
  private Long amountRemaining;

  @NonNull
  @JsonProperty("status")
  private PaymentLinkStatus status;

  @NonNull
  @JsonProperty("createdAt")
  private String createdAt;

  @NonNull
  @JsonProperty("transactions")
  private List<Transaction> transactions;

  @JsonProperty("cancellationReason")
  private String cancellationReason;

  @JsonProperty("canceledAt")
  private String canceledAt;
}
