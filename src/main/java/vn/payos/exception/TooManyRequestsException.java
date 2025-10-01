package vn.payos.exception;

/** TooManyRequestsException */
public class TooManyRequestsException extends APIException {
  /**
   * TooManyRequestsException
   *
   * @param message message
   */
  public TooManyRequestsException(String message) {
    super(message);
  }

  /**
   * TooManyRequestsException
   *
   * @param message message
   * @param statusCode status code
   * @param errorCode code
   * @param errorDesc desc
   */
  public TooManyRequestsException(
      String message, Integer statusCode, String errorCode, String errorDesc) {
    super(message, statusCode, errorCode, errorDesc);
  }

  /**
   * TooManyRequestsException
   *
   * @param message message
   * @param cause cause
   * @param statusCode status code
   * @param errorCode code
   * @param errorDesc desc
   */
  public TooManyRequestsException(
      String message, Throwable cause, Integer statusCode, String errorCode, String errorDesc) {
    super(message, cause, statusCode, errorCode, errorDesc);
  }
}
