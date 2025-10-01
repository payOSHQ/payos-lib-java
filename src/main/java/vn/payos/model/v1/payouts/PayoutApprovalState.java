package vn.payos.model.v1.payouts;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** PayoutApprovalState */
@Getter
@AllArgsConstructor
public enum PayoutApprovalState {
  /** DRAFTING */
  DRAFTING("DRAFTING"),
  /** SUBMITTED */
  SUBMITTED("SUBMITTED"),
  /** APPROVED */
  APPROVED("APPROVED"),
  /** REJECTED */
  REJECTED("REJECTED"),
  /** CANCELLED */
  CANCELLED("CANCELLED"),
  /** SCHEDULED */
  SCHEDULED("SCHEDULED"),
  /** PROCESSING */
  PROCESSING("PROCESSING"),
  /** FAILED */
  FAILED("FAILED"),
  /** PARTIAL_COMPLETED */
  PARTIAL_COMPLETED("PARTIAL_COMPLETED"),
  /** COMPLETED */
  COMPLETED("COMPLETED");

  @JsonValue private final String value;

  /**
   * Get value from string
   *
   * @param value value string
   * @return value
   */
  public static PayoutApprovalState fromValue(String value) {
    for (PayoutApprovalState item : PayoutApprovalState.values()) {
      if (item.value.equals(value.toUpperCase())) {
        return item;
      }
    }
    throw new IllegalArgumentException("Invalid value: " + value);
  }
}
