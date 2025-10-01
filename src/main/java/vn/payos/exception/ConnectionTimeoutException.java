package vn.payos.exception;

/** ConnectionTimeoutException */
public class ConnectionTimeoutException extends PayOSException {
  /**
   * ConnectionTimeoutException
   *
   * @param message message
   */
  public ConnectionTimeoutException(String message) {
    super(message);
  }

  /**
   * ConnectionTimeoutException
   *
   * @param message message
   * @param cause cause
   */
  public ConnectionTimeoutException(String message, Throwable cause) {
    super(message, cause);
  }
}
