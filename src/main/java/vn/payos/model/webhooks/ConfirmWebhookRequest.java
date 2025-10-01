package vn.payos.model.webhooks;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** ConfirmWebhookRequest */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmWebhookRequest {
  @NonNull
  @JsonProperty("webhookUrl")
  private String webhookUrl;
}
