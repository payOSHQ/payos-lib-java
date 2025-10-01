package vn.payos.exception;

/** BadRequestException */
public class BadRequestException extends APIException {

  /**
   * BadRequestException
   *
   * @param message message
   */
  public BadRequestException(String message) {
    super(message);
  }

  /**
   * BadRequestException
   *
   * @param message message
   * @param statusCode status code
   * @param errorCode code
   * @param errorDesc desc
   */
  public BadRequestException(
      String message, Integer statusCode, String errorCode, String errorDesc) {
    super(message, statusCode, errorCode, errorDesc);
  }

  /**
   * BadRequestException
   *
   * @param message message
   * @param cause cause
   * @param statusCode status code
   * @param errorCode code
   * @param errorDesc desc
   */
  public BadRequestException(
      String message, Throwable cause, Integer statusCode, String errorCode, String errorDesc) {
    super(message, cause, statusCode, errorCode, errorDesc);
  }
}
