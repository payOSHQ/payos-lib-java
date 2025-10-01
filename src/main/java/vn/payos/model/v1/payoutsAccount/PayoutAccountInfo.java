package vn.payos.model.v1.payoutsAccount;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** PayoutAccountInfo */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutAccountInfo {
  @NonNull
  @JsonProperty("accountNumber")
  private String accountNumber;

  @NonNull
  @JsonProperty("accountName")
  private String accountName;

  @NonNull
  @JsonProperty("currency")
  private String currency;

  @NonNull
  @JsonProperty("balance")
  private String balance;
}
