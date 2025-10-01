package vn.payos.exception;

/** InternalServerException */
public class InternalServerException extends APIException {
  /**
   * InternalServerException
   *
   * @param message message
   */
  public InternalServerException(String message) {
    super(message);
  }

  /**
   * InternalServerException
   *
   * @param message message
   * @param statusCode status code
   * @param errorCode code
   * @param errorDesc desc
   */
  public InternalServerException(
      String message, Integer statusCode, String errorCode, String errorDesc) {
    super(message, statusCode, errorCode, errorDesc);
  }

  /**
   * InternalServerException
   *
   * @param message message
   * @param cause cause
   * @param statusCode status code
   * @param errorCode code
   * @param errorDesc desc
   */
  public InternalServerException(
      String message, Throwable cause, Integer statusCode, String errorCode, String errorDesc) {
    super(message, cause, statusCode, errorCode, errorDesc);
  }
}
