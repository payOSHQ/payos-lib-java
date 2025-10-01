package vn.payos.model.v2.paymentRequests;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** PaymentLinkStatus */
@Getter
@AllArgsConstructor
public enum PaymentLinkStatus {
  /** PENDING */
  PENDING("PENDING"),
  /** CANCELLED */
  CANCELLED("CANCELLED"),
  /** UNDERPAID */
  UNDERPAID("UNDERPAID"),
  /** PAID */
  PAID("PAID"),
  /** EXPIRED */
  EXPIRED("EXPIRED"),
  /** PROCESSING */
  PROCESSING("PROCESSING"),
  /** FAILED */
  FAILED("FAILED");

  @JsonValue private final String value;

  /**
   * Get value from string
   *
   * @param value value string
   * @return value
   */
  public static PaymentLinkStatus fromValue(String value) {
    for (PaymentLinkStatus item : PaymentLinkStatus.values()) {
      if (item.value.equals(value.toUpperCase())) {
        return item;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }
}
