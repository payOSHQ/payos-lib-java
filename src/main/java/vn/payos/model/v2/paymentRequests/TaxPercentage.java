package vn.payos.model.v2.paymentRequests;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** TaxPercentage */
@Getter
@AllArgsConstructor
public enum TaxPercentage {
  /** MINUS_TWO */
  MINUS_TWO(-2),
  /** MINUS_ONE */
  MINUS_ONE(-1),
  /** ZERO */
  ZERO(0),
  /** FIVE */
  FIVE(5),
  /** TEN */
  TEN(10);

  @JsonValue private final int value;

  /**
   * Get value from integer
   *
   * @param value value integer
   * @return value
   */
  public static TaxPercentage fromValue(int value) {
    for (TaxPercentage item : TaxPercentage.values()) {
      if (item.value == value) {
        return item;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }
}
