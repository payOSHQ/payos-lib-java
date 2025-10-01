package vn.payos.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;

/** DataConverterUtils */
public class DataConverterUtils {
  private static final ObjectMapper mapper;

  static {
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /**
   * Converts an object to a specified target class using Jackson's ObjectMapper. Supports various
   * input types including the target class itself, String (JSON), Map, and JsonNode.
   *
   * @param obj The object to normalize/convert
   * @param targetClass The class to convert the object to
   * @param <T> The type parameter representing the target class
   * @return An instance of the target class
   * @throws IllegalArgumentException if the input object type is not supported
   */
  public static <T> T normalize(Object obj, Class<T> targetClass) {
    if (targetClass.isInstance(obj)) {
      return targetClass.cast(obj);
    } else if (obj instanceof String) {
      try {
        return mapper.readValue((String) obj, targetClass);
      } catch (IOException e) {
        throw new IllegalArgumentException(
            "Cannot parse JSON string to " + targetClass.getSimpleName(), e);
      }
    } else if (obj instanceof Map) {
      return mapper.convertValue(obj, targetClass);
    } else if (obj instanceof com.fasterxml.jackson.databind.JsonNode) {
      try {
        return mapper.treeToValue((com.fasterxml.jackson.databind.JsonNode) obj, targetClass);
      } catch (JsonProcessingException e) {
        throw new IllegalArgumentException(
            "Cannot convert JsonNode to " + targetClass.getSimpleName(), e);
      }
    }
    throw new IllegalArgumentException("Unsupported type: " + obj.getClass());
  }
}
