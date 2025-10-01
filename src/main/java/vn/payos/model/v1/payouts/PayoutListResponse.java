package vn.payos.model.v1.payouts;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import vn.payos.core.PageableResponse;
import vn.payos.core.Pagination;

/** PayoutListResponse */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutListResponse implements PageableResponse<Payout> {
  @NonNull
  @JsonProperty("pagination")
  private Pagination pagination;

  @NonNull
  @JsonProperty("payouts")
  private List<Payout> payouts;

  @Override
  public List<Payout> getItems() {
    return payouts;
  }
}
