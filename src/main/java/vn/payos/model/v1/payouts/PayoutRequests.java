package vn.payos.model.v1.payouts;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** PayoutRequests */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutRequests {
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

  @JsonProperty("category")
  private List<String> category;
}
