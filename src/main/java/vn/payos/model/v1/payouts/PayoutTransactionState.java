package vn.payos.model.v1.payouts;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** PayoutTransactionState */
@Getter
@AllArgsConstructor
public enum PayoutTransactionState {
  /** RECEIVED */
  RECEIVED("RECEIVED"),
  /** PROCESSING */
  PROCESSING("PROCESSING"),
  /** CANCELLED */
  CANCELLED("CANCELLED"),
  /** SUCCEEDED */
  SUCCEEDED("SUCCEEDED"),
  /** ON_HOLD */
  ON_HOLD("ON_HOLD"),
  /** REVERSED */
  REVERSED("REVERSED"),
  /** FAILED */
  FAILED("FAILED");

  @JsonValue private final String value;

  /**
   * Get value from string
   *
   * @param value value string
   * @return value
   */
  public static PayoutTransactionState fromValue(String value) {
    for (PayoutTransactionState item : PayoutTransactionState.values()) {
      if (item.value.equals(value.toUpperCase())) {
        return item;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }
}
