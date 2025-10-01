package vn.payos.exception;

import java.util.Optional;

/** APIException */
public class APIException extends PayOSException {
  /** Status code */
  private final Integer statusCode;

  /** Code */
  private final String errorCode;

  /** Desc */
  private final String errorDesc;

  /**
   * APIException
   *
   * @param message message
   */
  public APIException(String message) {
    this(message, null, null, null);
  }

  /**
   * APIException
   *
   * @param message message
   * @param statusCode status code
   * @param errorCode code
   * @param errorDesc desc
   */
  public APIException(String message, Integer statusCode, String errorCode, String errorDesc) {
    super(message);
    this.statusCode = statusCode;
    this.errorCode = errorCode;
    this.errorDesc = errorDesc;
  }

  /**
   * APIException
   *
   * @param message message
   * @param cause cause
   * @param statusCode status code
   * @param errorCode code
   * @param errorDesc desc
   */
  public APIException(
      String message, Throwable cause, Integer statusCode, String errorCode, String errorDesc) {
    super(message, cause);
    this.statusCode = statusCode;
    this.errorCode = errorCode;
    this.errorDesc = errorDesc;
  }

  /**
   * Get status code
   *
   * @return status code
   */
  public Optional<Integer> getStatusCode() {
    return Optional.ofNullable(statusCode);
  }

  /**
   * Get code
   *
   * @return code
   */
  public Optional<String> getErrorCode() {
    return Optional.ofNullable(errorCode);
  }

  /**
   * Get desc
   *
   * @return desc
   */
  public Optional<String> getErrorDesc() {
    return Optional.ofNullable(errorDesc);
  }

  /**
   * Create an APIException or its specific subclass based on the status code.
   *
   * @param statusCode status code
   * @param errorCode code
   * @param errorDesc desc
   * @param message message
   * @return Exception
   */
  public static APIException fromResponse(
      int statusCode, String errorCode, String errorDesc, String message) {
    String finalMessage =
        (message != null && !message.isEmpty())
            ? message
            : (errorDesc != null && !errorDesc.isEmpty())
                ? errorDesc
                : "HTTP " + statusCode + " error";

    switch (statusCode) {
      case 400:
        return new BadRequestException(finalMessage, statusCode, errorCode, errorDesc);
      case 401:
        return new UnauthorizedException(finalMessage, statusCode, errorCode, errorDesc);
      case 403:
        return new ForbiddenException(finalMessage, statusCode, errorCode, errorDesc);
      case 404:
        return new NotFoundException(finalMessage, statusCode, errorCode, errorDesc);
      case 429:
        return new TooManyRequestsException(finalMessage, statusCode, errorCode, errorDesc);
      default:
        if (statusCode >= 500) {
          return new InternalServerException(finalMessage, statusCode, errorCode, errorDesc);
        } else {
          return new APIException(finalMessage, statusCode, errorCode, errorDesc);
        }
    }
  }
}
