package vn.payos.model.v1.payouts.batch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** PayoutBatchItem */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutBatchItem {
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
}
