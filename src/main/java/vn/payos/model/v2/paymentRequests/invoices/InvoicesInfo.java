package vn.payos.model.v2.paymentRequests.invoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;

/** InvoicesInfo */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoicesInfo {
  @NonNull
  @Singular
  @JsonProperty("invoices")
  private List<Invoice> invoices;
}
