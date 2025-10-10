package vn.payos.model.v2.paymentRequests.invoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Invoice */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
  @NonNull
  @JsonProperty("invoiceId")
  private String invoiceId;

  @JsonProperty("invoiceNumber")
  private String invoiceNumber;

  @JsonProperty("issuedTimestamp")
  private Long issuedTimestamp;

  @JsonProperty("issuedDatetime")
  private String issuedDatetime;

  @JsonProperty("transactionId")
  private String transactionId;

  @JsonProperty("reservationCode")
  private String reservationCode;

  @JsonProperty("codeOfTax")
  private String codeOfTax;
}
