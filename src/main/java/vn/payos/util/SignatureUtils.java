package vn.payos.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.payos.type.PaymentData;

/**
 * Utilities class for create HMAC-SHA256 signature
 */
public class SignatureUtils {
  private static String convertObjToQueryStr(Object object) {
    StringBuilder stringBuilder = new StringBuilder();
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> map = objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {
    });

    map.forEach((key, value) -> {
      String valueAsString = "";
      if (value == null) {
        valueAsString = "";
      } else if (value instanceof List) {
        List<Object> sortedList = new ArrayList<>();
        ((List<?>) value).forEach(item -> sortedList.add(sortObjDataByKey(item)));
        try {
          valueAsString = objectMapper.writeValueAsString(sortedList);
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      } else {
        valueAsString = value.toString();
      }

      if (stringBuilder.length() > 0) {
        stringBuilder.append('&');
      }
      stringBuilder.append(key).append('=').append(valueAsString);
    });
    return stringBuilder.toString();
  }

  private static Object sortObjDataByKey(Object object) {
    ObjectMapper objectMapper = new ObjectMapper();
    // Convert the object to a Map
    Map<String, Object> map = objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {
    });

    // Sort the map by keys
    TreeMap<String, Object> sortedMap = new TreeMap<>(map);

    // Convert the sorted map back to an Object
    return objectMapper.convertValue(sortedMap, Object.class);
  }

  private static String generateHmacSHA256(String dataStr, String key)
      throws NoSuchAlgorithmException, InvalidKeyException {
    Mac sha256Hmac = Mac.getInstance("HmacSHA256");
    SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    sha256Hmac.init(secretKey);
    byte[] hmacBytes = sha256Hmac.doFinal(dataStr.getBytes(StandardCharsets.UTF_8));

    // Chuyển byte array sang chuỗi hex
    StringBuilder hexStringBuilder = new StringBuilder();
    for (byte b : hmacBytes) {
      hexStringBuilder.append(String.format("%02x", b));
    }
    return hexStringBuilder.toString();
  }

  /**
   * Create HMAC-SHA256 signature from an object
   * 
   * @param object Object to create signature
   * @param key    Checksum Key of PayOS payment channel
   * @return HMAC-SHA256 signature
   * @throws NoSuchAlgorithmException Create signature failed
   * @throws InvalidKeyException      Invalid checksum key
   */
  public static String createSignatureFromObj(Object object, String key)
      throws NoSuchAlgorithmException, InvalidKeyException {
    Object sortedDataByKey = sortObjDataByKey(object);
    String dataQueryStr = convertObjToQueryStr(sortedDataByKey);
    return generateHmacSHA256(dataQueryStr, key);
  }

  /**
   * Create HMAC-SHA256 signature from payment data
   * 
   * @param data Payment data
   * @param key  Checksum Key of PayOS payment channel
   * @return HMAC-SHA256 signature
   * @throws NoSuchAlgorithmException Create signature failed
   * @throws InvalidKeyException      Invalid checksum key
   */
  public static String createSignatureOfPaymentRequest(PaymentData data, String key)
      throws NoSuchAlgorithmException, InvalidKeyException {
    int amount = data.getAmount();
    String cancelUrl = data.getCancelUrl();
    String description = data.getDescription();
    String orderCode = Long.toString(data.getOrderCode());
    String returnUrl = data.getReturnUrl();

    String dataStr = "amount=" + amount + "&cancelUrl=" + cancelUrl + "&description=" + description
        + "&orderCode=" + orderCode + "&returnUrl=" + returnUrl;
    // Sử dụng HMAC-SHA-256 để tính toán chữ ký
    return generateHmacSHA256(dataStr, key);
  }
}
