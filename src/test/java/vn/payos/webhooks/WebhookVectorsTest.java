package vn.payos.webhooks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import vn.payos.PayOS;
import vn.payos.exception.WebhookException;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;

class WebhookVectorsTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final JsonNode VECTORS = loadVectors();
  private static final PayOS PAYOS = new PayOS("c", "a", VECTORS.get("checksumKey").asText());

  private static JsonNode loadVectors() {
    try (InputStream in =
        WebhookVectorsTest.class.getResourceAsStream("/vn/payos/webhooks/webhook-vectors.json")) {
      return MAPPER.readTree(in);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  static Stream<Arguments> allCases() {
    List<Arguments> args = new ArrayList<>();
    for (JsonNode c : VECTORS.get("cases")) {
      args.add(Arguments.of(c.get("id").asText(), c));
    }
    return args.stream();
  }

  static Stream<Arguments> typedCases() {
    return allCases().filter(a -> ((JsonNode) a.get()[1]).get("typed").asBoolean());
  }

  private static boolean isAccept(JsonNode c) {
    return "accept".equals(c.get("expect").asText());
  }

  private static long bodyOrderCode(JsonNode c) throws IOException {
    return MAPPER.readTree(c.get("body").asText()).get("data").get("orderCode").asLong();
  }

  // R14/R15 are signature-mismatch cases; every other reject must come from validation.
  private static void assertRejectMessage(String id, Throwable e) {
    if (!id.startsWith("R14") && !id.startsWith("R15")) {
      assertTrue(e.getMessage().startsWith("Invalid webhook data"), id + ": " + e.getMessage());
    }
  }

  private static Webhook readTyped(String body) throws JsonProcessingException {
    return MAPPER.readValue(body, Webhook.class);
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("allCases")
  void blockingWire(String id, JsonNode c) throws IOException {
    String body = c.get("body").asText();
    if (isAccept(c)) {
      WebhookData data = PAYOS.webhooks().verify(body);
      assertEquals(bodyOrderCode(c), data.getOrderCode());
    } else {
      WebhookException e =
          assertThrows(WebhookException.class, () -> PAYOS.webhooks().verify(body), id);
      assertRejectMessage(id, e);
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("allCases")
  void asyncWire(String id, JsonNode c) throws IOException {
    String body = c.get("body").asText();
    if (isAccept(c)) {
      WebhookData data = PAYOS.async().webhooks().verify(body).join();
      assertEquals(bodyOrderCode(c), data.getOrderCode());
    } else {
      CompletionException e =
          assertThrows(
              CompletionException.class, () -> PAYOS.async().webhooks().verify(body).join(), id);
      assertInstanceOf(WebhookException.class, e.getCause(), id);
      assertRejectMessage(id, e.getCause());
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("typedCases")
  void blockingTyped(String id, JsonNode c) throws IOException {
    String body = c.get("body").asText();
    Webhook webhook;
    try {
      webhook = readTyped(body);
    } catch (JsonProcessingException e) {
      assertTrue(!isAccept(c), id + ": deserialization failed for accept case");
      return;
    }
    if (isAccept(c)) {
      assertEquals(bodyOrderCode(c), PAYOS.webhooks().verify(webhook).getOrderCode());
    } else {
      assertThrows(WebhookException.class, () -> PAYOS.webhooks().verify(webhook), id);
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("typedCases")
  void asyncTyped(String id, JsonNode c) throws IOException {
    String body = c.get("body").asText();
    Webhook webhook;
    try {
      webhook = readTyped(body);
    } catch (JsonProcessingException e) {
      assertTrue(!isAccept(c), id + ": deserialization failed for accept case");
      return;
    }
    if (isAccept(c)) {
      assertEquals(
          bodyOrderCode(c), PAYOS.async().webhooks().verify(webhook).join().getOrderCode());
    } else {
      CompletionException e =
          assertThrows(
              CompletionException.class, () -> PAYOS.async().webhooks().verify(webhook).join(), id);
      assertInstanceOf(WebhookException.class, e.getCause(), id);
    }
  }

  @Test
  void emptyStringIsRejected() {
    assertThrows(WebhookException.class, () -> PAYOS.webhooks().verify(""));
  }

  @Test
  void jsonArrayIsRejected() {
    assertThrows(WebhookException.class, () -> PAYOS.webhooks().verify("[]"));
  }
}
