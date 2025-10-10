package vn.payos.model.v1.payouts;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** GetPayoutListParams */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPayoutListParams {
  @JsonProperty("referenceId")
  private String referenceId;

  @JsonProperty("approvalState")
  private PayoutApprovalState approvalState;

  @JsonProperty("category")
  private List<String> category;

  @JsonProperty("fromDate")
  private String fromDate;

  @JsonProperty("toDate")
  private String toDate;

  @JsonProperty("limit")
  private Integer limit;

  @JsonProperty("offset")
  private Integer offset;
}
