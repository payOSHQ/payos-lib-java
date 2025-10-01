package vn.payos.model.v2.paymentRequests;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;

/** CreatePaymentLinkRequest */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentLinkRequest {
  @NonNull
  @JsonProperty("orderCode")
  private Long orderCode;

  @NonNull
  @JsonProperty("amount")
  private Long amount;

  @NonNull
  @JsonProperty("description")
  private String description;

  @NonNull
  @JsonProperty("cancelUrl")
  private String cancelUrl;

  @NonNull
  @JsonProperty("returnUrl")
  private String returnUrl;

  @JsonProperty("signature")
  private String signature;

  @JsonProperty("items")
  @Singular()
  private List<PaymentLinkItem> items;

  @JsonProperty("buyerName")
  private String buyerName;

  @JsonProperty("buyerCompanyName")
  private String buyerCompanyName;

  @JsonProperty("buyerTaxCode")
  private String buyerTaxCode;

  @JsonProperty("buyerEmail")
  private String buyerEmail;

  @JsonProperty("buyerPhone")
  private String buyerPhone;

  @JsonProperty("buyerAddress")
  private String buyerAddress;

  @JsonProperty("invoice")
  private InvoiceRequest invoice;

  @JsonProperty("expiredAt")
  private Long expiredAt;
}
