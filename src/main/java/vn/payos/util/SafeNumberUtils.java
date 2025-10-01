package vn.payos.util;

/** SafeNumberUtils */
public class SafeNumberUtils {

  /** The maximum safe integer value. This is 2^53 - 1 = 9,007,199,254,740,991 */
  public static final long MAX_SAFE_INTEGER = 9007199254740991L;

  /** The minimum safe integer value. This is -(2^53 - 1) = -9,007,199,254,740,991 */
  public static final long MIN_SAFE_INTEGER = -9007199254740991L;

  private SafeNumberUtils() {}

  /**
   * Validate safe number
   *
   * @param value value
   * @return result
   */
  public static boolean isSafeNumber(Long value) {
    if (value == null) {
      return false;
    }
    return isSafeNumber(value.longValue());
  }

  /**
   * Validate safe number
   *
   * @param value value
   * @return result
   */
  public static boolean isSafeNumber(long value) {
    return value >= MIN_SAFE_INTEGER && value <= MAX_SAFE_INTEGER;
  }

  /**
   * Validate safe number
   *
   * @param value value
   * @return result
   */
  public static boolean isSafeNumber(Integer value) {
    return value != null;
  }

  /**
   * Validate safe number
   *
   * @param value value
   * @return result
   */
  public static boolean isSafeNumber(int value) {
    return true;
  }

  /**
   * Validate safe number
   *
   * @param value value
   * @return result
   */
  public static boolean isSafeNumber(Double value) {
    if (value == null) {
      return false;
    }
    return isSafeNumber(value.doubleValue());
  }

  /**
   * Validate safe number
   *
   * @param value value
   * @return result
   */
  public static boolean isSafeNumber(double value) {
    return Double.isFinite(value);
  }

  /**
   * Validate safe number
   *
   * @param value value
   * @return result
   */
  public static boolean isSafeNumber(Float value) {
    if (value == null) {
      return false;
    }
    return isSafeNumber(value.floatValue());
  }

  /**
   * Validate safe number
   *
   * @param value value
   * @return result
   */
  public static boolean isSafeNumber(float value) {
    return Float.isFinite(value);
  }

  /**
   * Validate safe number
   *
   * @param value value
   * @return result
   */
  public static boolean isSafeNumber(String value) {
    if (value == null || value.trim().isEmpty()) {
      return false;
    }

    try {
      String trimmed = value.trim();

      // Try parsing as long first
      if (!trimmed.contains(".") && !trimmed.toLowerCase().contains("e")) {
        long longValue = Long.parseLong(trimmed);
        return isSafeNumber(longValue);
      } else {
        // Parse as double
        double doubleValue = Double.parseDouble(trimmed);
        return isSafeNumber(doubleValue);
      }
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Get max safe integer
   *
   * @return max safe integer
   */
  public static long getMaxSafeInteger() {
    return MAX_SAFE_INTEGER;
  }

  /**
   * Get min safe integer
   *
   * @return min safe integer
   */
  public static long getMinSafeInteger() {
    return MIN_SAFE_INTEGER;
  }

  /**
   * Clamp to safe range
   *
   * @param value value
   * @return result
   */
  public static long clampToSafeRange(long value) {
    if (value > MAX_SAFE_INTEGER) {
      return MAX_SAFE_INTEGER;
    } else if (value < MIN_SAFE_INTEGER) {
      return MIN_SAFE_INTEGER;
    }
    return value;
  }

  /**
   * Validate safe number
   *
   * @param value value
   * @param fieldName field name
   * @throws IllegalArgumentException exception
   */
  public static void validateSafeNumber(Long value, String fieldName)
      throws IllegalArgumentException {
    if (!isSafeNumber(value)) {
      throw new IllegalArgumentException(
          String.format(
              "%s value %s is not safe. " + "Must be between %d and %d",
              fieldName, value, MIN_SAFE_INTEGER, MAX_SAFE_INTEGER));
    }
  }

  /**
   * Validate safe number
   *
   * @param value value
   * @param fieldName field name
   * @throws IllegalArgumentException exception
   */
  public static void validateSafeNumber(long value, String fieldName)
      throws IllegalArgumentException {
    if (!isSafeNumber(value)) {
      throw new IllegalArgumentException(
          String.format(
              "%s value %d is not safe. " + "Must be between %d and %d",
              fieldName, value, MIN_SAFE_INTEGER, MAX_SAFE_INTEGER));
    }
  }
}
