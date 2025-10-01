package vn.payos.model.v2.paymentRequests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** PaymentLinkItem */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLinkItem {
  @NonNull
  @JsonProperty("name")
  private String name;

  @NonNull
  @JsonProperty("quantity")
  private Integer quantity;

  @NonNull
  @JsonProperty("price")
  private Long price;

  @JsonProperty("unit")
  private String unit;

  @JsonProperty("taxPercentage")
  private TaxPercentage taxPercentage;
}
