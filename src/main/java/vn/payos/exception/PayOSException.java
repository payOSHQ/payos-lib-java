package vn.payos.exception;

/** PayOSException */
public class PayOSException extends RuntimeException {
  /**
   * PayOSException
   *
   * @param message message
   */
  public PayOSException(String message) {
    super(message);
  }

  /**
   * PayOSException
   *
   * @param message message
   * @param cause cause
   */
  public PayOSException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * PayOSException
   *
   * @param cause cause
   */
  public PayOSException(Throwable cause) {
    super(cause);
  }
}
