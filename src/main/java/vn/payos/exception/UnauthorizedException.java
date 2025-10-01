package vn.payos.exception;

/** UnauthorizedException */
public class UnauthorizedException extends APIException {
  /**
   * UnauthorizedException
   *
   * @param message message
   */
  public UnauthorizedException(String message) {
    super(message);
  }

  /**
   * UnauthorizedException
   *
   * @param message message
   * @param statusCode status code
   * @param errorCode code
   * @param errorDesc desc
   */
  public UnauthorizedException(
      String message, Integer statusCode, String errorCode, String errorDesc) {
    super(message, statusCode, errorCode, errorDesc);
  }

  /**
   * UnauthorizedException
   *
   * @param message message
   * @param cause cause
   * @param statusCode status code
   * @param errorCode code
   * @param errorDesc desc
   */
  public UnauthorizedException(
      String message, Throwable cause, Integer statusCode, String errorCode, String errorDesc) {
    super(message, cause, statusCode, errorCode, errorDesc);
  }
}
