package vn.payos.model.v2.paymentRequests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** CancelPaymentLinkRequest */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentLinkRequest {
  @JsonProperty("cancellationReason")
  private String cancellationReason;
}
