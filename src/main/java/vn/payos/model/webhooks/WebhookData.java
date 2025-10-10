package vn.payos.model.webhooks;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** WebhookData */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookData {
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
  @JsonProperty("accountNumber")
  private String accountNumber;

  @NonNull
  @JsonProperty("reference")
  private String reference;

  @NonNull
  @JsonProperty("transactionDateTime")
  private String transactionDateTime;

  @NonNull
  @JsonProperty("currency")
  private String currency;

  @NonNull
  @JsonProperty("paymentLinkId")
  private String paymentLinkId;

  @NonNull
  @JsonProperty("code")
  private String code;

  @NonNull
  @JsonProperty("desc")
  private String desc;

  @JsonProperty("counterAccountBankId")
  private String counterAccountBankId;

  @JsonProperty("counterAccountBankName")
  private String counterAccountBankName;

  @JsonProperty("counterAccountName")
  private String counterAccountName;

  @JsonProperty("counterAccountNumber")
  private String counterAccountNumber;

  @JsonProperty("virtualAccountName")
  private String virtualAccountName;

  @JsonProperty("virtualAccountNumber")
  private String virtualAccountNumber;
}
