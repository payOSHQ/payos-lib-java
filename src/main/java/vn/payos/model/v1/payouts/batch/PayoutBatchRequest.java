package vn.payos.model.v1.payouts.batch;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;

/** PayoutBatchRequest */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutBatchRequest {
  @NonNull
  @JsonProperty("referenceId")
  private String referenceId;

  @JsonProperty("validateDestination")
  private boolean validateDestination;

  @JsonProperty("category")
  private List<String> category;

  @NonNull
  @JsonProperty("payouts")
  @Singular
  private List<PayoutBatchItem> payouts;
}
