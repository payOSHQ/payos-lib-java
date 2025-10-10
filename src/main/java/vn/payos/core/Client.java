package vn.payos.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.*;
import lombok.Getter;
import lombok.NonNull;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import vn.payos.crypto.CryptoProvider;
import vn.payos.crypto.CryptoProviderImpl;
import vn.payos.exception.APIException;
import vn.payos.exception.InvalidSignatureException;
import vn.payos.exception.PayOSException;
import vn.payos.util.PropertiesUtils;

/**
 * Base client class that provides common functionality for both synchronous and asynchronous payOS
 * implementations.
 */
public abstract class Client {
  /** options */
  @Getter protected final ClientOptions options;

  /** VERSION */
  public static final String VERSION = PropertiesUtils.getPackageVersion();

  /** httpClient */
  protected final OkHttpClient httpClient;

  /** mapper */
  protected final ObjectMapper mapper = new ObjectMapper();

  /** crypto */
  protected final CryptoProvider crypto;

  /**
   * Client
   *
   * @param clientId client id
   * @param apiKey api key
   * @param checksumKey checksum key
   */
  public Client(@NonNull String clientId, @NonNull String apiKey, @NonNull String checksumKey) {
    this(new ClientOptions(clientId, apiKey, checksumKey));
  }

  /**
   * Client
   *
   * @param clientId client id
   * @param apiKey api key
   * @param checksumKey checksum key
   * @param partnerCode partner code
   */
  public Client(
      @NonNull String clientId,
      @NonNull String apiKey,
      @NonNull String checksumKey,
      String partnerCode) {
    this(new ClientOptions(clientId, apiKey, checksumKey, partnerCode));
  }

  /**
   * Client
   *
   * @param options options
   */
  public Client(@NonNull ClientOptions options) {
    this.options = options;
    this.httpClient = options.getHttpClient();
    this.crypto = new CryptoProviderImpl();
  }

  /**
   * Get crypto
   *
   * @return crypto
   */
  public CryptoProvider getCrypto() {
    return crypto;
  }

  /**
   * Build headers
   *
   * @param addl addition headers
   * @return headers
   */
  protected Map<String, String> buildHeaders(Map<String, String> addl) {
    Map<String, String> headers = new LinkedHashMap<>();
    headers.put("x-client-id", options.getClientId());
    headers.put("x-api-key", options.getApiKey());
    headers.put("Content-Type", "application/json");
    headers.put("User-Agent", getUserAgent());
    if (options.getPartnerCode() != null && !options.getPartnerCode().isEmpty())
      headers.put("x-partner-code", options.getPartnerCode());
    if (addl != null) headers.putAll(addl);
    return headers;
  }

  /**
   * Get user-agent
   *
   * @return user-agent
   */
  protected String getUserAgent() {
    return "PayOS/Java " + VERSION;
  }

  /**
   * Build URL
   *
   * @param path path
   * @param queries queries
   * @return URL
   */
  protected String buildUrl(String path, Map<String, Object> queries) {
    try {
      HttpUrl.Builder builder = HttpUrl.parse(options.getBaseURL()).newBuilder();
      if (path != null && !path.isEmpty()) {
        String normalizePath = path.startsWith("/") ? path.substring(1) : path;
        String[] segments = normalizePath.split("/");
        for (String segment : segments) {
          if (!segment.isEmpty()) {
            builder.addPathSegment(segment);
          }
        }
      }

      if (queries != null && !queries.isEmpty()) {
        for (Map.Entry<String, Object> entry : queries.entrySet()) {
          Object value = entry.getValue();
          if (value == null) {
            builder.addQueryParameter(entry.getKey(), "");
          } else {
            String stringValue;
            if (value instanceof Collection || value.getClass().isArray() || value instanceof Map) {
              try {
                stringValue = mapper.writeValueAsString(value);
              } catch (Exception ex) {
                stringValue = String.valueOf(value);
              }
            } else {
              stringValue = String.valueOf(value);
            }
            builder.addQueryParameter(entry.getKey(), stringValue);
          }
        }
      }

      return builder.build().toString();
    } catch (Exception e) {
      throw new PayOSException("Failed to build URL: " + e.getMessage(), e);
    }
  }

  /**
   * Should retry
   *
   * @param status status
   * @return should retry
   */
  protected boolean shouldRetry(int status) {
    if (status == 408 || status == 429) return true;
    return status >= 500;
  }

  /**
   * Build body
   *
   * @param <TReq> Request body type
   * @param body body
   * @return body
   */
  protected <TReq> String buildBody(TReq body) {
    if (body == null) return null;
    if (body instanceof String) return (String) body;
    try {
      return mapper.writeValueAsString(body);
    } catch (Exception e) {
      throw new PayOSException(e);
    }
  }

  /**
   * Parse JSON
   *
   * @param <T> Parsed type
   * @param json JSON
   * @param ref type reference
   * @return Parsed type
   */
  protected <T> T parseJson(String json, TypeReference<T> ref) {
    try {
      return mapper.readValue(json, ref);
    } catch (IOException e) {
      throw new PayOSException(e);
    }
  }

  /**
   * Apply signature headers
   *
   * @param headers headers
   * @param sig signature
   * @param body body
   */
  protected void applySignatureHeaders(
      Map<String, String> headers, FinalRequestOptions.SignatureOptions sig, Object body) {
    if (sig == null) return;
    if (sig.getRequest() == FinalRequestOptions.RequestSigning.HEADER) {
      String signature =
          crypto.createSignature(options.getChecksumKey(), body == null ? new Object() : body);
      headers.put("x-signature", signature);
    }
  }

  /**
   * Validate response signature
   *
   * @param apiResp API Response
   * @param sig signature
   * @param data data
   * @param response response
   */
  protected void validateResponseSignature(
      APIResponse<?> apiResp,
      FinalRequestOptions.SignatureOptions sig,
      Object data,
      Response response) {
    if (sig == null || sig.getResponse() == null) return;
    String resSignature;
    if (sig.getResponse() == FinalRequestOptions.ResponseSigning.BODY) {
      resSignature = apiResp.getSignature();
    } else {
      resSignature = response.header("x-signature");
    }
    if (resSignature == null || resSignature.isEmpty()) {
      throw new InvalidSignatureException("Data integrity check failed");
    }
    String signed =
        (sig.getResponse() == FinalRequestOptions.ResponseSigning.BODY)
            ? crypto.createSignatureFromObj(data, options.getChecksumKey())
            : crypto.createSignature(options.getChecksumKey(), data);
    if (!resSignature.equals(signed)) {
      throw new InvalidSignatureException("Data integrity check failed");
    }
  }

  /**
   * Sleep
   *
   * @param ms sleep time
   */
  protected void sleep(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Calculate retry time
   *
   * @param attempt attempt
   * @return retry time
   */
  protected long computeBackoffMs(int attempt) {
    double init = 0.5, max = 10.0;
    double secs = Math.min(init * Math.pow(2, attempt), max);
    double jitter = 1 - Math.random() * 0.25;
    return (long) (secs * jitter * 1000);
  }

  /**
   * Calculate retry delay based on response headers or fallback to exponential backoff
   *
   * @param response HTTP response with headers
   * @param attempt current retry attempt number
   * @param maxRetries maximum number of retries
   * @param retriesRemaining retries remaining
   * @return retry delay in milliseconds
   */
  protected long computeRetryDelayMs(
      Response response, int attempt, int maxRetries, int retriesRemaining) {
    Long timeoutMs = null;

    // Check Retry-After header
    String retryAfter = response.header("retry-after");
    if (retryAfter != null && !retryAfter.isEmpty()) {
      try {
        // Try to parse as seconds
        double timeoutSeconds = Double.parseDouble(retryAfter);
        if (!Double.isNaN(timeoutSeconds)) {
          timeoutMs = (long) (timeoutSeconds * 1000);
        }
      } catch (NumberFormatException e) {
        // Try to parse as HTTP date
        try {
          java.text.SimpleDateFormat sdf =
              new java.text.SimpleDateFormat(
                  "EEE, dd MMM yyyy HH:mm:ss zzz", java.util.Locale.ENGLISH);
          java.util.Date retryDate = sdf.parse(retryAfter);
          timeoutMs = retryDate.getTime() - System.currentTimeMillis();
        } catch (java.text.ParseException ignored) {
          // Unable to parse, will fall back to exponential backoff
        }
      }
    }

    // Check x-ratelimit-reset header
    if (timeoutMs == null) {
      String rateLimitReset = response.header("x-ratelimit-reset");
      if (rateLimitReset != null && !rateLimitReset.isEmpty()) {
        try {
          double timeoutSeconds = Double.parseDouble(rateLimitReset);
          if (!Double.isNaN(timeoutSeconds)) {
            timeoutMs = (long) (timeoutSeconds * 1000) - System.currentTimeMillis();
          }
        } catch (NumberFormatException ignored) {
          // Unable to parse, will fall back to exponential backoff
        }
      }
    }

    // Use header-based timeout if valid and reasonable (between 0 and 60 seconds)
    if (timeoutMs != null && timeoutMs >= 0 && timeoutMs < 60 * 1000) {
      return timeoutMs;
    }

    // Fallback to exponential backoff with jitter
    double initRetryDelay = 0.5;
    double maxRetryDelay = 10.0;
    int numRetries = maxRetries - retriesRemaining;
    double sleepSeconds = Math.min(initRetryDelay * Math.pow(2, numRetries), maxRetryDelay);
    // Apply some jitter to avoid thunder herd
    double jitter = 1 - Math.random() * 0.25;
    return (long) (sleepSeconds * jitter * 1000);
  }

  /**
   * Build request
   *
   * @param method method
   * @param url URL
   * @param body body
   * @param headers headers
   * @return request
   */
  protected Request buildRequest(
      FinalRequestOptions.HTTPMethod method, String url, String body, Map<String, String> headers) {
    Request.Builder requestBuilder = new Request.Builder().url(url);

    if (headers != null) {
      for (Map.Entry<String, String> header : headers.entrySet()) {
        requestBuilder.addHeader(header.getKey(), header.getValue());
      }
    }

    RequestBody requestBody = null;
    if (body != null && !body.isEmpty()) {
      requestBody = RequestBody.create(body, MediaType.parse("application/json"));
    }

    switch (method) {
      case GET:
        requestBuilder.get();
        break;
      case POST:
        requestBuilder.post(requestBody != null ? requestBody : RequestBody.create(new byte[0]));
        break;
      case PUT:
        requestBuilder.put(requestBody != null ? requestBody : RequestBody.create(new byte[0]));
        break;
      case PATCH:
        requestBuilder.patch(requestBody != null ? requestBody : RequestBody.create(new byte[0]));
        break;
      case DELETE:
        if (requestBody != null) {
          requestBuilder.delete(requestBody);
        } else {
          requestBuilder.delete();
        }
        break;
      default:
        requestBuilder.get();
        break;
    }

    return requestBuilder.build();
  }

  /**
   * Process request body with signature
   *
   * @param <TReq> Request body type
   * @param reqOpts request options
   * @return request
   */
  protected <TReq> String processRequestBodyWithSignature(FinalRequestOptions<TReq> reqOpts) {
    String body = buildBody(reqOpts.getBody());

    if (reqOpts.getSignatureOpts() != null && reqOpts.getSignatureOpts().getRequest() != null) {
      switch (reqOpts.getSignatureOpts().getRequest()) {
        case CREATE_PAYMENT_LINK:
          if (reqOpts.getBody() != null) {
            try {
              Map<String, Object> map =
                  mapper.convertValue(
                      reqOpts.getBody(), new TypeReference<Map<String, Object>>() {});
              String sig =
                  crypto.createSignatureOfPaymentRequest(
                      reqOpts.getBody(), this.options.getChecksumKey());
              map.put("signature", sig);
              body = mapper.writeValueAsString(map);
            } catch (Exception e) {
              throw new PayOSException(e);
            }
          }
          break;
        case BODY:
          if (reqOpts.getBody() != null) {
            try {
              Map<String, Object> map =
                  mapper.convertValue(
                      reqOpts.getBody(), new TypeReference<Map<String, Object>>() {});
              String sig =
                  crypto.createSignatureFromObj(reqOpts.getBody(), this.options.getChecksumKey());
              map.put("signature", sig);
              body = mapper.writeValueAsString(map);
            } catch (Exception e) {
              throw new PayOSException(e);
            }
          }
          break;
        case HEADER:
          // set later in applySignatureHeaders
          break;
      }
    }

    return body;
  }

  /**
   * Process response
   *
   * @param <TResp> Response type
   * @param text text
   * @param status status
   * @param res res
   * @param reqOpts response options
   * @param dataClass dataClass
   * @return response
   */
  protected <TResp> TResp processResponse(
      String text,
      int status,
      Response res,
      FinalRequestOptions<?> reqOpts,
      Class<TResp> dataClass) {
    APIResponse<TResp> apiResp = parseJson(text, new TypeReference<APIResponse<TResp>>() {});
    if (!"00".equals(apiResp.getCode()) || apiResp.getData() == null) {
      throw APIException.fromResponse(
          status, apiResp.getCode(), apiResp.getDesc(), apiResp.getDesc());
    }
    validateResponseSignature(apiResp, reqOpts.getSignatureOpts(), apiResp.getData(), res);
    return mapper.convertValue(apiResp.getData(), dataClass);
  }

  /**
   * Create APIException
   *
   * @param text text
   * @param status status
   * @return APIException
   */
  protected APIException createAPIException(String text, int status) {
    String errorCode = null;
    String errorDesc = null;
    try {
      TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
      Map<String, Object> errObj = mapper.readValue(text, typeRef);
      Object code = errObj.get("code");
      Object desc = errObj.get("desc");
      errorCode = code != null ? String.valueOf(code) : null;
      errorDesc = desc != null ? String.valueOf(desc) : null;
    } catch (Exception e) {
      errorDesc = text;
    }
    return APIException.fromResponse(status, errorCode, errorDesc, text);
  }

  /**
   * Process file download response
   *
   * @param res res
   * @return file
   * @throws IOException exception
   */
  protected FileDownloadResponse processFileDownloadResponse(Response res) throws IOException {
    String contentType = res.header("content-type");
    if (contentType == null) contentType = "application/octet-stream";

    // Check if response is JSON (error response)
    if (contentType.contains("application/json")) {
      String text = "";
      if (res.body() != null) {
        text = res.body().string();
      }

      // Parse JSON to extract error details
      try {
        TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
        Map<String, Object> errorResponse = mapper.readValue(text, typeRef);
        Object code = errorResponse.get("code");
        Object desc = errorResponse.get("desc");
        String errorCode = code != null ? String.valueOf(code) : null;
        String errorDesc = desc != null ? String.valueOf(desc) : null;
        throw APIException.fromResponse(res.code(), errorCode, errorDesc, text);
      } catch (APIException e) {
        throw e;
      } catch (Exception e) {
        // If JSON parsing fails, throw with raw response
        throw APIException.fromResponse(res.code(), null, text, text);
      }
    }

    String disposition = res.header("content-disposition");
    String length = res.header("content-length");
    String filename = null;
    if (disposition != null) {
      int idx = disposition.toLowerCase().indexOf("filename=");
      if (idx >= 0) {
        filename = disposition.substring(idx + 9).replaceAll("[\"']", "");
      }
    }

    byte[] data = new byte[0];
    if (res.body() != null) {
      data = res.body().bytes();
    }

    Long size = length != null ? Long.parseLong(length) : null;
    return FileDownloadResponse.builder()
        .filename(filename)
        .contentType(contentType)
        .size(size)
        .data(data)
        .build();
  }
}
