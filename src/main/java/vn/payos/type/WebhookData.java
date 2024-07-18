package vn.payos.type;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

/** Webhook data */
@Getter
@Setter
@Builder
@Jacksonized
public class WebhookData {
  @NonNull
  private Long orderCode;
  @NonNull
  private Integer amount;
  @NonNull
  private String description;
  @NonNull
  private String accountNumber;
  @NonNull
  private String reference;
  @NonNull
  private String transactionDateTime;
  @NonNull
  private String currency;
  @NonNull
  private String paymentLinkId;
  @NonNull
  private String code;
  @NonNull
  private String desc;
  private String counterAccountBankId;
  private String counterAccountBankName;
  private String counterAccountName;
  private String counterAccountNumber;
  private String virtualAccountName;
  private String virtualAccountNumber;
}
