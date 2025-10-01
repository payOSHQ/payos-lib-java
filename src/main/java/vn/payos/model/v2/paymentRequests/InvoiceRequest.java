package vn.payos.model.v2.paymentRequests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** InvoiceRequest */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {
  @JsonProperty("buyerNotGetInvoice")
  private Boolean buyerNotGetInvoice;

  @JsonProperty("taxPercentage")
  private TaxPercentage taxPercentage;
}
