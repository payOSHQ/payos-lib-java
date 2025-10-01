package vn.payos.exception;

/** ConnectionException */
public class ConnectionException extends PayOSException {

  /**
   * ConnectionException
   *
   * @param message message
   */
  public ConnectionException(String message) {
    super(message);
  }

  /**
   * ConnectionException
   *
   * @param message message
   * @param cause cause
   */
  public ConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
