package vn.payos.model.v2.paymentRequests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Transaction */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
  @NonNull
  @JsonProperty("reference")
  private String reference;

  @NonNull
  @JsonProperty("amount")
  private Long amount;

  @NonNull
  @JsonProperty("accountNumber")
  private String accountNumber;

  @NonNull
  @JsonProperty("description")
  private String description;

  @NonNull
  @JsonProperty("transactionDateTime")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  private OffsetDateTime transactionDateTime;

  @JsonProperty("virtualAccountName")
  private String virtualAccountName;

  @JsonProperty("virtualAccountNumber")
  private String virtualAccountNumber;

  @JsonProperty("counterAccountBankId")
  private String counterAccountBankId;

  @JsonProperty("counterAccountBankName")
  private String counterAccountBankName;

  @JsonProperty("counterAccountName")
  private String counterAccountName;

  @JsonProperty("counterAccountNumber")
  private String counterAccountNumber;
}
