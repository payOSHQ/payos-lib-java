package vn.payos.core;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/** FinalRequestOptions */
@Data
@Builder
public class FinalRequestOptions<T> {
  private HTTPMethod method;
  private String path;

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
   * Create from RequestOptions
   *
   * @param <T> Request body type
   * @param method method
   * @param path path
   * @param options options
   * @return FinalRequestOptions
   */
  public static <T> FinalRequestOptions<T> fromRequestOptions(
      HTTPMethod method, String path, RequestOptions<T> options) {
    SignatureOptions convertedSignatureOpts = null;
    if (options != null && options.getSignatureOpts() != null) {
      RequestOptions.SignatureOptions sourceOpts = options.getSignatureOpts();
      convertedSignatureOpts =
          SignatureOptions.builder()
              .request(convertRequestSigning(sourceOpts.getRequest()))
              .response(convertResponseSigning(sourceOpts.getResponse()))
              .build();
    }

    return FinalRequestOptions.<T>builder()
        .method(method)
        .path(path)
        .body(options != null ? options.getBody() : null)
        .queries(options != null ? options.getQueries() : null)
        .headers(options != null ? options.getHeaders() : null)
        .maxRetries(options != null ? options.getMaxRetries() : null)
        .timeout(options != null ? options.getTimeout() : null)
        .signatureOpts(convertedSignatureOpts)
        .build();
  }

  private static RequestSigning convertRequestSigning(RequestOptions.RequestSigning source) {
    if (source == null) return null;
    switch (source) {
      case BODY:
        return RequestSigning.BODY;
      case HEADER:
        return RequestSigning.HEADER;
      case CREATE_PAYMENT_LINK:
        return RequestSigning.CREATE_PAYMENT_LINK;
      default:
        return null;
    }
  }

  private static ResponseSigning convertResponseSigning(RequestOptions.ResponseSigning source) {
    if (source == null) return null;
    switch (source) {
      case BODY:
        return ResponseSigning.BODY;
      case HEADER:
        return ResponseSigning.HEADER;
      default:
        return null;
    }
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
