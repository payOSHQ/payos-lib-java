package vn.payos.core;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/** RequestOptions */
@Data
@Builder
public class RequestOptions<T> {
  @Singular private Map<String, Object> queries;

  private T body;

  @Singular private Map<String, String> headers;

  private Integer maxRetries; // default 2
  private Integer timeout; // ms
  private SignatureOptions signatureOpts;

  /** SignatureOptions */
  @Data
  @Builder
  public static class SignatureOptions {
    private RequestSigning request; // body | header | create-payment-link
    private ResponseSigning response; // body | header
  }

  /** HTTPMethod */
  public enum HTTPMethod {
    /** GET */
    GET,
    /** POST */
    POST,
    /** PUT */
    PUT,
    /** PATCH */
    PATCH,
    /** DELETE */
    DELETE
  }

  /** RequestSigning */
  public enum RequestSigning {
    /** BODY */
    BODY,
    /** HEADER */
    HEADER,
    /** CREATE_PAYMENT_LINK */
    CREATE_PAYMENT_LINK
  }

  /** ResponseSigning */
  public enum ResponseSigning {
    /** BODY */
    BODY,
    /** HEADER */
    HEADER
  }

  /**
   * Set header
   *
   * @param key key
   * @param value value
   */
  public void setHeader(String key, String value) {
    if (this.headers == null) {
      this.headers = new HashMap<>();
    } else if (!(this.headers instanceof HashMap)) {
      this.headers = new HashMap<>(this.headers);
    }
    this.headers.put(key, value);
  }

  /**
   * Set query
   *
   * @param key key
   * @param value value
   */
  public void setQuery(String key, Object value) {
    if (this.queries == null) {
      this.queries = new HashMap<>();
    } else if (!(this.queries instanceof HashMap)) {
      this.queries = new HashMap<>(this.queries);
    }
    this.queries.put(key, value);
  }
}
