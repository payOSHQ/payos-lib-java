package vn.payos.crypto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** SignatureOptions */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignatureOptions {
  @Builder.Default private Boolean encodeUri = true;

  @Builder.Default private Boolean sortArrays = false;

  @Builder.Default private Algorithm algorithm = Algorithm.SHA256;

  /** Algorithm */
  public enum Algorithm {
    /** SHA1 */
    SHA1("HmacSHA1"),
    /** SHA256 */
    SHA256("HmacSHA256"),
    /** SHA512 */
    SHA512("HmacSHA512"),
    /** MD5 */
    MD5("HmacMD5");

    private final String javaAlgorithmName;

    Algorithm(String javaAlgorithmName) {
      this.javaAlgorithmName = javaAlgorithmName;
    }

    /**
     * Get algorithm
     *
     * @return algorithm
     */
    public String getJavaAlgorithmName() {
      return javaAlgorithmName;
    }
  }
}
