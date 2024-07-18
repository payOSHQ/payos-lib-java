package vn.payos.type;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

/** Webhook */
@Getter
@Setter
@Builder
@Jacksonized
public class Webhook {
  @NonNull
  private String code;
  @NonNull
  private String desc;
  @NonNull
  private WebhookData data;
  @NonNull
  private String signature;
}
