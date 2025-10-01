package vn.payos.exception;

/** ForbiddenException */
public class ForbiddenException extends APIException {
  /**
   * ForbiddenException
   *
   * @param message message
   */
  public ForbiddenException(String message) {
    super(message);
  }

  /**
   * ForbiddenException
   *
   * @param message message
   * @param statusCode status code
   * @param errorCode code
   * @param errorDesc desc
   */
  public ForbiddenException(
      String message, Integer statusCode, String errorCode, String errorDesc) {
    super(message, statusCode, errorCode, errorDesc);
  }

  /**
   * ForbiddenException
   *
   * @param message message
   * @param cause cause
   * @param statusCode status code
   * @param errorCode code
   * @param errorDesc desc
   */
  public ForbiddenException(
      String message, Throwable cause, Integer statusCode, String errorCode, String errorDesc) {
    super(message, cause, statusCode, errorCode, errorDesc);
  }
}
