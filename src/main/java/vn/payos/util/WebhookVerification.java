package vn.payos.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import vn.payos.exception.WebhookException;
import vn.payos.model.webhooks.WebhookData;

/** Validation helpers for webhook signature verification. */
public final class WebhookVerification {
  private static final ObjectMapper STRICT = new ObjectMapper();

  static {
    STRICT.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
  }

  private static final Pattern KEY_PATTERN = Pattern.compile("[A-Za-z0-9_]+");
  private static final long MAX_SAFE_INTEGER = 9007199254740991L;
  private static final long MIN_SAFE_INTEGER = -9007199254740991L;
  private static final List<String> CORE_INT_KEYS = Arrays.asList("orderCode", "amount");
  private static final List<String> CORE_STRING_KEYS =
      Arrays.asList(
          "description",
          "accountNumber",
          "reference",
          "transactionDateTime",
          "currency",
          "paymentLinkId",
          "code",
          "desc");
  private static final List<String> OPTIONAL_KEYS =
      Arrays.asList(
          "counterAccountBankId",
          "counterAccountBankName",
          "counterAccountName",
          "counterAccountNumber",
          "virtualAccountName",
          "virtualAccountNumber");

  private WebhookVerification() {}

  /**
   * Convert webhook input to a JSON tree without going through the typed model.
   *
   * @param webhook raw JSON string, JsonNode or Map
   * @return JSON tree
   */
  public static JsonNode toTree(Object webhook) {
    if (webhook instanceof String) {
      try {
        return STRICT.readTree((String) webhook);
      } catch (IOException e) {
        throw new WebhookException("Invalid webhook data: malformed JSON", e);
      }
    }
    if (webhook instanceof JsonNode) {
      return (JsonNode) webhook;
    }
    if (webhook instanceof Map) {
      try {
        return STRICT.valueToTree(webhook);
      } catch (IllegalArgumentException e) {
        throw new WebhookException("Invalid webhook data", e);
      }
    }
    throw new WebhookException("Unsupported webhook type");
  }

  /**
   * Validate the wire form of webhook data.
   *
   * @param data webhook data node
   */
  public static void validateData(JsonNode data) {
    if (data == null || !data.isObject()) {
      throw invalid("data must be an object");
    }
    Iterator<String> names = data.fieldNames();
    while (names.hasNext()) {
      String key = names.next();
      if (!KEY_PATTERN.matcher(key).matches() || "__proto__".equals(key)) {
        throw invalid("invalid key");
      }
      JsonNode value = data.get(key);
      if (value.isNull()) {
        continue;
      }
      if (value.isTextual()) {
        if (value.asText().contains("=")) {
          throw invalid("value of " + key + " contains '='");
        }
        continue;
      }
      if (!isSafeInteger(value)) {
        throw invalid("unsupported value type for " + key);
      }
    }
    for (String key : CORE_INT_KEYS) {
      if (!isSafeInteger(data.get(key))) {
        throw invalid(key + " must be an integer");
      }
    }
    for (String key : CORE_STRING_KEYS) {
      JsonNode value = data.get(key);
      if (value == null || !value.isTextual()) {
        throw invalid(key + " must be a string");
      }
    }
    for (String key : OPTIONAL_KEYS) {
      JsonNode value = data.get(key);
      if (value != null && !value.isNull() && !value.isTextual()) {
        throw invalid(key + " must be a string or null");
      }
    }
  }

  /**
   * Validate values of a typed webhook data model.
   *
   * @param d webhook data
   */
  public static void validateTyped(WebhookData d) {
    if (d == null) {
      throw invalid("data is required");
    }
    if (!isSafeLong(d.getOrderCode()) || !isSafeLong(d.getAmount())) {
      throw invalid("orderCode and amount must be safe integers");
    }
    String[] values = {
      d.getDescription(),
      d.getAccountNumber(),
      d.getReference(),
      d.getTransactionDateTime(),
      d.getCurrency(),
      d.getPaymentLinkId(),
      d.getCode(),
      d.getDesc(),
      d.getCounterAccountBankId(),
      d.getCounterAccountBankName(),
      d.getCounterAccountName(),
      d.getCounterAccountNumber(),
      d.getVirtualAccountName(),
      d.getVirtualAccountNumber()
    };
    for (String value : values) {
      if (value != null && value.contains("=")) {
        throw invalid("value contains '='");
      }
    }
  }

  /**
   * Compare two strings in constant time.
   *
   * @param a first
   * @param b second
   * @return true when equal
   */
  public static boolean constantTimeEquals(String a, String b) {
    return a != null
        && b != null
        && MessageDigest.isEqual(
            a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
  }

  private static boolean isSafeInteger(JsonNode value) {
    if (value == null || !value.isIntegralNumber() || !value.canConvertToLong()) {
      return false;
    }
    long n = value.asLong();
    return n >= MIN_SAFE_INTEGER && n <= MAX_SAFE_INTEGER;
  }

  private static boolean isSafeLong(Long n) {
    return n != null && n >= MIN_SAFE_INTEGER && n <= MAX_SAFE_INTEGER;
  }

  private static WebhookException invalid(String reason) {
    return new WebhookException("Invalid webhook data: " + reason);
  }
}
