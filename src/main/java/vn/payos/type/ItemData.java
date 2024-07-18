package vn.payos.type;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

/** Item data */
@Getter
@Setter
@Builder
@Jacksonized
public class ItemData {
  @NonNull
  private String name;
  @NonNull
  private Integer quantity;
  @NonNull
  private Integer price;
}
