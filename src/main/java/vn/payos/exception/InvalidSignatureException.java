package vn.payos.exception;

/** InvalidSignatureException */
public class InvalidSignatureException extends PayOSException {
  /**
   * InvalidSignatureException
   *
   * @param message message
   */
  public InvalidSignatureException(String message) {
    super(message);
  }

  /**
   * InvalidSignatureException
   *
   * @param message message
   * @param cause cause
   */
  public InvalidSignatureException(String message, Throwable cause) {
    super(message, cause);
  }
}
