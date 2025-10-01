package vn.payos.model.v1.payouts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** EstimateCredit */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimateCredit {
  @NonNull
  @JsonProperty("estimateCredit")
  private Long estimateCredit;
}
