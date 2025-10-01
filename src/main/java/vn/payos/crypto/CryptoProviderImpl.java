package vn.payos.crypto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import vn.payos.exception.PayOSException;

/** CryptoProviderImpl */
public class CryptoProviderImpl implements CryptoProvider {

  @Override
  public String createSignatureFromObj(Object data, String key) {
    try {
      return createSignatureFromObject(data, key);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new PayOSException(e);
    }
  }

  @Override
  public String createSignatureOfPaymentRequest(Object data, String key) {
    try {
      return createSignatureFromPaymentRequest(data, key);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new PayOSException(e);
    }
  }

  @Override
  public String createSignature(String secretKey, Object jsonData) {
    return this.createSignature(secretKey, jsonData, null);
  }

  @Override
  public String createSignature(String secretKey, Object jsonData, SignatureOptions options) {
    try {
      return createSignatureWithOptions(secretKey, jsonData, options);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new PayOSException(e);
    }
  }

  @Override
  public String createUuidv4() {
    return UUID.randomUUID().toString();
  }

  private String createSignatureWithOptions(
      String secretKey, Object jsonData, SignatureOptions options)
      throws NoSuchAlgorithmException, InvalidKeyException {
    if (options == null) {
      options = SignatureOptions.builder().build();
    }

    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> dataMap =
        objectMapper.convertValue(jsonData, new TypeReference<Map<String, Object>>() {});

    Map<String, Object> sortedData = deepSortObj(dataMap, options.getSortArrays());

    StringBuilder queryString = new StringBuilder();
    boolean first = true;

    for (Map.Entry<String, Object> entry : sortedData.entrySet()) {
      if (!first) {
        queryString.append('&');
      }
      first = false;

      String key = entry.getKey();
      Object value = entry.getValue();

      String valueString = "";
      if (value == null) {
        valueString = "";
      } else if (value instanceof List
          || (value instanceof Map && !((Map<?, ?>) value).isEmpty())) {
        try {
          valueString = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
          valueString = value.toString();
        }
      } else {
        valueString = value.toString();
      }

      if (options.getEncodeUri()) {
        try {
          key = URLEncoder.encode(key, StandardCharsets.UTF_8.toString()).replace("+", "%20");
          valueString =
              URLEncoder.encode(valueString, StandardCharsets.UTF_8.toString()).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
          throw new PayOSException(e);
        }
      }

      queryString.append(key).append('=').append(valueString);
    }

    return generateHmac(queryString.toString(), secretKey, options.getAlgorithm());
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> deepSortObj(Map<String, Object> obj, boolean sortArrays) {
    Map<String, Object> result = new LinkedHashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();

    // Sort keys
    obj.keySet().stream()
        .sorted()
        .forEach(
            key -> {
              Object value = obj.get(key);

              if (value instanceof List) {
                List<?> list = (List<?>) value;
                if (sortArrays) {
                  // Sort array elements
                  List<Object> sortedList = new ArrayList<>();
                  for (Object item : list) {
                    if (item instanceof Map) {
                      sortedList.add(deepSortObj((Map<String, Object>) item, sortArrays));
                    } else {
                      sortedList.add(item);
                    }
                  }
                  // Sort the list elements
                  sortedList.sort(
                      (a, b) -> {
                        if (!(a instanceof Map) && !(b instanceof Map)) {
                          return String.valueOf(a).compareTo(String.valueOf(b));
                        }
                        try {
                          return objectMapper
                              .writeValueAsString(a)
                              .compareTo(objectMapper.writeValueAsString(b));
                        } catch (JsonProcessingException e) {
                          return 0;
                        }
                      });
                  result.put(key, sortedList);
                } else {
                  // Maintain array order but sort objects within arrays
                  List<Object> processedList = new ArrayList<>();
                  for (Object item : list) {
                    if (item instanceof Map) {
                      processedList.add(deepSortObj((Map<String, Object>) item, sortArrays));
                    } else {
                      processedList.add(item);
                    }
                  }
                  result.put(key, processedList);
                }
              } else if (value instanceof Map) {
                result.put(key, deepSortObj((Map<String, Object>) value, sortArrays));
              } else {
                result.put(key, value);
              }
            });

    return result;
  }

  private String generateHmac(String data, String key, SignatureOptions.Algorithm algorithm)
      throws NoSuchAlgorithmException, InvalidKeyException {
    Mac hmac = Mac.getInstance(algorithm.getJavaAlgorithmName());
    SecretKeySpec secretKey =
        new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm.getJavaAlgorithmName());
    hmac.init(secretKey);
    byte[] hmacBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

    StringBuilder hexStringBuilder = new StringBuilder();
    for (byte b : hmacBytes) {
      hexStringBuilder.append(String.format("%02x", b));
    }
    return hexStringBuilder.toString();
  }

  private String createSignatureFromObject(Object object, String key)
      throws NoSuchAlgorithmException, InvalidKeyException {
    Object sortedDataByKey = sortObjectDataByKey(object);
    String dataQueryStr = convertObjectToQueryString(sortedDataByKey);
    return generateHmacSHA256(dataQueryStr, key);
  }

  private String createSignatureFromPaymentRequest(Object data, String key)
      throws NoSuchAlgorithmException, InvalidKeyException {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> map =
        mapper.convertValue(data, new TypeReference<Map<String, Object>>() {});
    Object amount = map.get("amount");
    Object cancelUrl = map.get("cancelUrl");
    Object description = map.get("description");
    Object orderCode = map.get("orderCode");
    Object returnUrl = map.get("returnUrl");
    String dataStr =
        "amount="
            + String.valueOf(amount)
            + "&cancelUrl="
            + String.valueOf(cancelUrl)
            + "&description="
            + String.valueOf(description)
            + "&orderCode="
            + String.valueOf(orderCode)
            + "&returnUrl="
            + String.valueOf(returnUrl);
    return generateHmacSHA256(dataStr, key);
  }

  private String convertObjectToQueryString(Object object) {
    StringBuilder stringBuilder = new StringBuilder();
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> map =
        objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {});

    map.forEach(
        (key, value) -> {
          String valueAsString = "";
          if (value == null) {
            valueAsString = "";
          } else if (value instanceof List) {
            List<Object> sortedList = new ArrayList<>();
            ((List<?>) value).forEach(item -> sortedList.add(sortObjectDataByKey(item)));
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

  private Object sortObjectDataByKey(Object object) {
    ObjectMapper objectMapper = new ObjectMapper();
    // Convert the object to a Map
    Map<String, Object> map =
        objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {});

    // Sort the map by keys
    TreeMap<String, Object> sortedMap = new TreeMap<>(map);

    // Convert the sorted map back to an Object
    return objectMapper.convertValue(sortedMap, Object.class);
  }

  private String generateHmacSHA256(String dataStr, String key)
      throws NoSuchAlgorithmException, InvalidKeyException {
    Mac sha256Hmac = Mac.getInstance("HmacSHA256");
    SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    sha256Hmac.init(secretKey);
    byte[] hmacBytes = sha256Hmac.doFinal(dataStr.getBytes(StandardCharsets.UTF_8));

    // Convert byte array to hex string
    StringBuilder hexStringBuilder = new StringBuilder();
    for (byte b : hmacBytes) {
      hexStringBuilder.append(String.format("%02x", b));
    }
    return hexStringBuilder.toString();
  }
}
