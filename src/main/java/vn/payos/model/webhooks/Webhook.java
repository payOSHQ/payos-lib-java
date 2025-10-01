package vn.payos.model.webhooks;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Webhook */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Webhook {
  @NonNull
  @JsonProperty("code")
  private String code;

  @NonNull
  @JsonProperty("desc")
  private String desc;

  @JsonProperty("success")
  private Boolean success;

  @NonNull
  @JsonProperty("data")
  private WebhookData data;

  @NonNull
  @JsonProperty("signature")
  private String signature;
}
