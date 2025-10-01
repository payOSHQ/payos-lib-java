package vn.payos.exception;

/** NotFoundException */
public class NotFoundException extends APIException {
  /**
   * NotFoundException
   *
   * @param message message
   */
  public NotFoundException(String message) {
    super(message);
  }

  /**
   * NotFoundException
   *
   * @param message message
   * @param statusCode status code
   * @param errorCode code
   * @param errorDesc desc
   */
  public NotFoundException(String message, Integer statusCode, String errorCode, String errorDesc) {
    super(message, statusCode, errorCode, errorDesc);
  }

  /**
   * NotFoundException
   *
   * @param message message
   * @param cause cause
   * @param statusCode status code
   * @param errorCode code
   * @param errorDesc desc
   */
  public NotFoundException(
      String message, Throwable cause, Integer statusCode, String errorCode, String errorDesc) {
    super(message, cause, statusCode, errorCode, errorDesc);
  }
}
