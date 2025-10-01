package vn.payos.crypto;

/** CryptoProvider */
public interface CryptoProvider {
  /**
   * Create signature from object for payment-requests
   *
   * @param data data
   * @param key key
   * @return signature
   */
  String createSignatureFromObj(Object data, String key);

  /**
   * Create signature for create payment link
   *
   * @param data data
   * @param key key
   * @return signature
   */
  String createSignatureOfPaymentRequest(Object data, String key);

  /**
   * Create signature for payouts
   *
   * @param secretKey key
   * @param jsonData data
   * @return signature
   */
  String createSignature(String secretKey, Object jsonData);

  /**
   * Create signature for payouts
   *
   * @param secretKey key
   * @param jsonData data
   * @param options additional option
   * @return signature
   */
  String createSignature(String secretKey, Object jsonData, SignatureOptions options);

  /**
   * Create UUIDv4
   *
   * @return UUIDv4
   */
  String createUuidv4();
}
