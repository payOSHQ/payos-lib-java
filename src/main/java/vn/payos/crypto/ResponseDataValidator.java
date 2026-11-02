package vn.payos.crypto;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import vn.payos.exception.InvalidSignatureException;

/**
 * Validates the shape of body-signed response {@code data} before its signature is computed.
 *
 * <p>The legacy body signature is computed over {@code key=value} pairs joined by {@code &} without
 * any encoding, with lists serialised as JSON. The received data must therefore not be able to
 * shift delimiters, replace a scalar with an object, or turn a list into a string that swallows the
 * remaining fields. Top-level values are scalars or lists; objects are only accepted inside lists
 * and may only contain scalars.
 */
public final class ResponseDataValidator {
  private static final Pattern KEY_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");
  // A "&key=" sequence inside a value is what lets the unencoded signed string be re-split.
  private static final Pattern SMUGGLING_PATTERN = Pattern.compile("&[A-Za-z0-9_]+=");
  private static final long MAX_SAFE_INTEGER = 9007199254740991L;

  private ResponseDataValidator() {}

  private static InvalidSignatureException invalid(String reason) {
    return new InvalidSignatureException("Invalid response data: " + reason);
  }

  /**
   * Validate body-signed response data as parsed by Jackson (maps, lists and scalars).
   *
   * @param data the {@code data} field of the API response
   * @throws InvalidSignatureException when the data does not have the expected shape
   */
  public static void validate(Object data) {
    if (!(data instanceof Map)) {
      throw invalid("data must be a JSON object");
    }
    Map<?, ?> map = (Map<?, ?>) data;
    validateKeys("", map);
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      String key = String.valueOf(entry.getKey());
      Object value = entry.getValue();
      if (!(value instanceof List)) {
        validateScalar(key, value);
        continue;
      }
      List<?> list = (List<?>) value;
      for (int i = 0; i < list.size(); i++) {
        String path = key + "[" + i + "]";
        Object item = list.get(i);
        if (item instanceof Map) {
          Map<?, ?> entryMap = (Map<?, ?>) item;
          validateKeys(path + ".", entryMap);
          for (Map.Entry<?, ?> field : entryMap.entrySet()) {
            validateScalar(path + "." + field.getKey(), field.getValue());
          }
        } else {
          validateScalar(path, item);
        }
      }
    }
  }

  private static void validateKeys(String path, Map<?, ?> record) {
    for (Object k : record.keySet()) {
      String key = String.valueOf(k);
      if (!(k instanceof String)
          || !KEY_PATTERN.matcher(key).matches()
          || key.equals("__proto__")) {
        throw invalid("unsupported key \"" + path + key + "\"");
      }
    }
  }

  private static void validateScalar(String path, Object value) {
    if (value == null || value instanceof Boolean) {
      return;
    }
    if (value instanceof Integer || value instanceof Long || value instanceof Short) {
      if (Math.abs(((Number) value).longValue()) > MAX_SAFE_INTEGER) {
        throw invalid("\"" + path + "\" must be a safe integer");
      }
    } else if (value instanceof BigInteger) {
      if (((BigInteger) value).abs().compareTo(BigInteger.valueOf(MAX_SAFE_INTEGER)) > 0) {
        throw invalid("\"" + path + "\" must be a safe integer");
      }
    } else if (value instanceof Double || value instanceof Float) {
      double d = ((Number) value).doubleValue();
      if (Double.isNaN(d)
          || Double.isInfinite(d)
          || (d == Math.floor(d) && Math.abs(d) > MAX_SAFE_INTEGER)) {
        throw invalid("\"" + path + "\" must be a finite number within the safe integer range");
      }
    } else if (value instanceof String) {
      if (SMUGGLING_PATTERN.matcher((String) value).find()) {
        throw invalid("\"" + path + "\" must not contain a \"&key=\" sequence");
      }
    } else {
      throw invalid("\"" + path + "\" has an unsupported type");
    }
  }
}
