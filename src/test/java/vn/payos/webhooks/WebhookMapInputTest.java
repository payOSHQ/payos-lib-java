package vn.payos.webhooks;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.Test;
import vn.payos.PayOS;
import vn.payos.exception.WebhookException;

class WebhookMapInputTest {
  private static final PayOS PAYOS = new PayOS("c", "a", "k");

  private static Map<String, Object> nullKeyWebhook() {
    return Collections.singletonMap("data", Collections.singletonMap(null, "x"));
  }

  @Test
  void blockingNullKeyMapIsRejected() {
    assertThrows(WebhookException.class, () -> PAYOS.webhooks().verify(nullKeyWebhook()));
  }

  @Test
  void asyncNullKeyMapIsRejected() {
    CompletionException e =
        assertThrows(
            CompletionException.class,
            () -> PAYOS.async().webhooks().verify(nullKeyWebhook()).join());
    assertInstanceOf(WebhookException.class, e.getCause());
  }
}
