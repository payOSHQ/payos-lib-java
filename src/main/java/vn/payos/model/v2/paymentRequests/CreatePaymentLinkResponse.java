package vn.payos.model.v2.paymentRequests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** CreatePaymentLinkResponse */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentLinkResponse {
  @NonNull
  @JsonProperty("bin")
  private String bin;

  @NonNull
  @JsonProperty("accountNumber")
  private String accountNumber;

  @NonNull
  @JsonProperty("accountName")
  private String accountName;

  @NonNull
  @JsonProperty("amount")
  private Long amount;

  @NonNull
  @JsonProperty("description")
  private String description;

  @NonNull
  @JsonProperty("orderCode")
  private Long orderCode;

  @NonNull
  @JsonProperty("currency")
  private String currency;

  @NonNull
  @JsonProperty("paymentLinkId")
  private String paymentLinkId;

  @NonNull
  @JsonProperty("status")
  private PaymentLinkStatus status;

  @JsonProperty("expiredAt")
  private Long expiredAt;

  @NonNull
  @JsonProperty("checkoutUrl")
  private String checkoutUrl;

  @NonNull
  @JsonProperty("qrCode")
  private String qrCode;
}
