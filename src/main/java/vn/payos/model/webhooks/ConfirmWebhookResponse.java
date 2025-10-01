package vn.payos.model.webhooks;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** ConfirmWebhookResponse */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmWebhookResponse {
  @NonNull
  @JsonProperty("webhookUrl")
  private String webhookUrl;

  @NonNull
  @JsonProperty("accountName")
  private String accountName;

  @NonNull
  @JsonProperty("accountNumber")
  private String accountNumber;

  @NonNull
  @JsonProperty("name")
  private String name;

  @NonNull
  @JsonProperty("shortName")
  private String shortName;
}
