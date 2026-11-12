package vn.payos.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import vn.payos.exception.PayOSException;

/**
 * Safe path segments for identifiers interpolated into API paths.
 *
 * <p>Identifiers are interpolated into paths such as {@code /v2/payment-requests/{id}/cancel}.
 * Without encoding, a value like {@code ../../v1/payouts-account/balance} or {@code x?} re-targets
 * the authenticated request to another endpoint, so strings are percent-encoded and {@code .} /
 * {@code ..} segments are rejected. Numbers must be non-negative and within the safe integer range.
 */
public final class PathSegments {
  private static final long MAX_SAFE_INTEGER = 9007199254740991L;

  private PathSegments() {}

  /**
   * Encode value as a single URL path segment.
   *
   * @param name parameter name used in error messages
   * @param value a String or a Long
   * @return the encoded segment
   * @throws PayOSException when the value is null/empty, a dot segment, or out of range
   */
  public static String segment(String name, Object value) {
    if (value instanceof Long || value instanceof Integer) {
      long n = ((Number) value).longValue();
      if (n < 0 || n > MAX_SAFE_INTEGER) {
        throw new PayOSException(name + " must be a non-negative safe integer");
      }
      return Long.toString(n);
    }
    if (!(value instanceof String) || ((String) value).isEmpty()) {
      throw new PayOSException(name + " must be a non-empty string or a safe integer");
    }
    String s = (String) value;
    if (s.equals(".") || s.equals("..")) {
      throw new PayOSException(name + " must not be a dot segment");
    }
    try {
      return URLEncoder.encode(s, "UTF-8").replace("+", "%20").replace("%7E", "~");
    } catch (UnsupportedEncodingException e) {
      throw new PayOSException(name + " could not be encoded", e);
    }
  }
}
