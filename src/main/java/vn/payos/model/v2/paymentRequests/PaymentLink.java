package vn.payos.model.v2.paymentRequests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
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
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime createdAt;

  @NonNull
  @JsonProperty("transactions")
  private List<Transaction> transactions;

  @JsonProperty("cancellationReason")
  private String cancellationReason;

  @JsonProperty("canceledAt")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime canceledAt;
}
