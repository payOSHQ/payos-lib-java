package vn.payos.exception;

/** WebhookException */
public class WebhookException extends PayOSException {
  /**
   * WebhookException
   *
   * @param message message
   */
  public WebhookException(String message) {
    super(message);
  }

  /**
   * WebhookException
   *
   * @param message message
   * @param cause cause
   */
  public WebhookException(String message, Throwable cause) {
    super(message, cause);
  }
}
