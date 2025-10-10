package vn.payos.model.v1.payouts;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Payout */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payout {
  @NonNull
  @JsonProperty("id")
  private String id;

  @NonNull
  @JsonProperty("referenceId")
  private String referenceId;

  @NonNull
  @JsonProperty("transactions")
  private List<PayoutTransaction> transactions;

  @JsonProperty("category")
  private List<String> category;

  @NonNull
  @JsonProperty("approvalState")
  private PayoutApprovalState approvalState;

  @NonNull
  @JsonProperty("createdAt")
  private String createdAt;
}
