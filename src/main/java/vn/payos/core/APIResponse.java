package vn.payos.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** APIResponse */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse<T> {
  private String code;
  private String desc;
  private T data;
  private String signature;
}
