package vn.payos.type;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

/** PayOS response */
@Getter
@Setter
@Builder
@Jacksonized
public class PayOSResponse<T> {
  @NonNull
  private String code;
  @NonNull
  private String desc;
  private T data;
  private String signature;
}
