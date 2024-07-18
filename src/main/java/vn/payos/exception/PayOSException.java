package vn.payos.exception;

import lombok.Getter;

/** PayOS Exception */
public class PayOSException extends Exception {
  /**
   * Error code
   */
  @Getter
  private String code;

  /**
   * Create PayOS exception with code and message
   * 
   * @param code    Error code
   * @param message Error message
   */
  public PayOSException(String code, String message) {
    super(message);
    this.code = code;
  }
}