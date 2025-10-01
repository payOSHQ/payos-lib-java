package vn.payos;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import vn.payos.core.Client;
import vn.payos.core.ClientOptions;
import vn.payos.core.FileDownloadResponse;
import vn.payos.core.FinalRequestOptions;
import vn.payos.core.FinalRequestOptions.HTTPMethod;
import vn.payos.core.RequestOptions;
import vn.payos.exception.ConnectionException;
import vn.payos.service.blocking.v1.payouts.PayoutsService;
import vn.payos.service.blocking.v1.payouts.PayoutsServiceImpl;
import vn.payos.service.blocking.v1.payoutsAccount.PayoutsAccountService;
import vn.payos.service.blocking.v1.payoutsAccount.PayoutsAccountServiceImpl;
import vn.payos.service.blocking.v2.paymentRequests.PaymentRequestsService;
import vn.payos.service.blocking.v2.paymentRequests.PaymentRequestsServiceImpl;
import vn.payos.service.blocking.webhooks.WebhooksService;
import vn.payos.service.blocking.webhooks.WebhooksServiceImpl;

/** payOS client */
public class PayOS extends Client {
  /**
   * payOS client
   *
   * @param clientId client id
   * @param apiKey api key
   * @param checksumKey checksum key
   */
  public PayOS(@NonNull String clientId, @NonNull String apiKey, @NonNull String checksumKey) {
    super(clientId, apiKey, checksumKey);
  }

  /**
   * payOS client
   *
   * @param clientId client id
   * @param apiKey api key
   * @param checksumKey checksum key
   * @param partnerCode partner code
   */
  public PayOS(
      @NonNull String clientId,
      @NonNull String apiKey,
      @NonNull String checksumKey,
      String partnerCode) {
    super(clientId, apiKey, checksumKey, partnerCode);
  }

  /**
   * payOS client
   *
   * @param options options
   */
  public PayOS(@NonNull ClientOptions options) {
    super(options);
  }

  /**
   * payOS client
   *
   * @return payOS client
   */
  public static PayOS fromEnv() {
    return new PayOS(ClientOptions.fromEnv());
  }

  /**
   * payOS async client
   *
   * @return payOS async client
   */
  public PayOSAsync async() {
    return new PayOSAsync(this.options);
  }

  /**
   * Make request
   *
   * @param <TResp> response body type
   * @param <TReq> request body type
   * @param options options
   * @param dataClass response data class
   * @return response
   */
  public <TResp, TReq> TResp request(FinalRequestOptions<TReq> options, Class<TResp> dataClass) {
    return execute(options, dataClass);
  }

  /**
   * Make GET request
   *
   * @param <TResp> response body type
   * @param path path
   * @param dataClass response data class
   * @param options options
   * @return response
   */
  public <TResp> TResp get(String path, Class<TResp> dataClass, RequestOptions<Void> options) {
    FinalRequestOptions<Void> finalOpts =
        FinalRequestOptions.fromRequestOptions(HTTPMethod.GET, path, options);
    return request(finalOpts, dataClass);
  }

  /**
   * Make GET request
   *
   * @param <TResp> response body type
   * @param path path
   * @param dataClass response data class
   * @return response
   */
  public <TResp> TResp get(String path, Class<TResp> dataClass) {
    FinalRequestOptions<?> finalOpts =
        FinalRequestOptions.builder().path(path).method(HTTPMethod.GET).build();
    return request(finalOpts, dataClass);
  }

  /**
   * Make POST request
   *
   * @param <TResp> response body type
   * @param <TReq> request body type
   * @param path path
   * @param dataClass response data class
   * @param options options
   * @return response
   */
  public <TResp, TReq> TResp post(
      String path, Class<TResp> dataClass, RequestOptions<TReq> options) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.fromRequestOptions((HTTPMethod.POST), path, options);
    return request(finalOpts, dataClass);
  }

  /**
   * Make POST request
   *
   * @param <TResp> response body type
   * @param <TReq> request body type
   * @param path path
   * @param dataClass response data class
   * @return response
   */
  public <TResp, TReq> TResp post(String path, Class<TResp> dataClass) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.<TReq>builder().path(path).method(HTTPMethod.POST).build();
    return request(finalOpts, dataClass);
  }

  /**
   * Make PUT request
   *
   * @param <TResp> response body type
   * @param <TReq> request body type
   * @param path path
   * @param dataClass response data class
   * @param options options
   * @return response
   */
  public <TResp, TReq> TResp put(
      String path, Class<TResp> dataClass, RequestOptions<TReq> options) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.fromRequestOptions((HTTPMethod.PUT), path, options);
    return request(finalOpts, dataClass);
  }

  /**
   * Make PUT request
   *
   * @param <TResp> response body type
   * @param <TReq> request body type
   * @param path path
   * @param dataClass response data class
   * @return response
   */
  public <TResp, TReq> TResp put(String path, Class<TResp> dataClass) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.<TReq>builder().path(path).method(HTTPMethod.PUT).build();
    return request(finalOpts, dataClass);
  }

  /**
   * Make PATCH request
   *
   * @param <TResp> response body type
   * @param <TReq> request body type
   * @param path path
   * @param dataClass response data class
   * @param options options
   * @return response
   */
  public <TResp, TReq> TResp patch(
      String path, Class<TResp> dataClass, RequestOptions<TReq> options) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.fromRequestOptions((HTTPMethod.PATCH), path, options);
    return request(finalOpts, dataClass);
  }

  /**
   * Make PATCH request
   *
   * @param <TResp> response body type
   * @param <TReq> request body type
   * @param path path
   * @param dataClass response data class
   * @return response
   */
  public <TResp, TReq> TResp patch(String path, Class<TResp> dataClass) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.<TReq>builder().path(path).method(HTTPMethod.PATCH).build();
    return request(finalOpts, dataClass);
  }

  /**
   * Make DELETE request
   *
   * @param <TResp> response body type
   * @param <TReq> request body type
   * @param path path
   * @param dataClass response data class
   * @param options options
   * @return response
   */
  public <TResp, TReq> TResp delete(
      String path, Class<TResp> dataClass, RequestOptions<TReq> options) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.fromRequestOptions((HTTPMethod.DELETE), path, options);
    return request(finalOpts, dataClass);
  }

  /**
   * Make DELETE request
   *
   * @param <TResp> response body type
   * @param <TReq> request body type
   * @param path path
   * @param dataClass response data class
   * @return response
   */
  public <TResp, TReq> TResp delete(String path, Class<TResp> dataClass) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.<TReq>builder().path(path).method(HTTPMethod.DELETE).build();
    return request(finalOpts, dataClass);
  }

  private <TResp, TReq> TResp execute(FinalRequestOptions<TReq> reqOpts, Class<TResp> dataClass) {
    int retriesRemaining =
        reqOpts.getMaxRetries() != null ? reqOpts.getMaxRetries() : this.options.getMaxRetries();
    int maxRetries =
        reqOpts.getMaxRetries() != null ? reqOpts.getMaxRetries() : this.options.getMaxRetries();
    FinalRequestOptions.HTTPMethod method =
        reqOpts.getMethod() != null ? reqOpts.getMethod() : FinalRequestOptions.HTTPMethod.GET;
    String url = buildUrl(reqOpts.getPath(), reqOpts.getQueries());
    String body = processRequestBodyWithSignature(reqOpts);
    java.util.Map<String, String> headers = buildHeaders(reqOpts.getHeaders());

    // Use per-request timeout if specified, otherwise use default httpClient
    OkHttpClient clientToUse = httpClient;
    if (reqOpts.getTimeout() != null && reqOpts.getTimeout() != httpClient.connectTimeoutMillis()) {
      clientToUse =
          httpClient
              .newBuilder()
              .connectTimeout(reqOpts.getTimeout(), TimeUnit.MILLISECONDS)
              .readTimeout(reqOpts.getTimeout(), TimeUnit.MILLISECONDS)
              .writeTimeout(reqOpts.getTimeout(), TimeUnit.MILLISECONDS)
              .build();
    }

    while (true) {
      applySignatureHeaders(headers, reqOpts.getSignatureOpts(), reqOpts.getBody());
      Request req = buildRequest(method, url, body, headers);
      try (Response res = clientToUse.newCall(req).execute()) {
        int status = res.code();
        String text = "";
        if (res.body() != null) {
          text = res.body().string();
        }

        if (status < 200 || status >= 300) {
          if (retriesRemaining > 0 && shouldRetry(status)) {
            retriesRemaining -= 1;
            sleep(
                computeRetryDelayMs(
                    res, maxRetries - retriesRemaining, maxRetries, retriesRemaining));
            continue;
          }
          throw createAPIException(text, status);
        }

        return processResponse(text, status, res, reqOpts, dataClass);
      } catch (IOException e) {
        if (retriesRemaining > 0) {
          retriesRemaining -= 1;
          sleep(computeBackoffMs(maxRetries - retriesRemaining));
          continue;
        }
        throw new ConnectionException(e.getMessage(), e);
      }
    }
  }

  /**
   * Download file
   *
   * @param <TReq> request body type
   * @param reqOpts options
   * @return File information
   */
  public <TReq> FileDownloadResponse downloadFile(FinalRequestOptions<TReq> reqOpts) {
    int retriesRemaining =
        reqOpts.getMaxRetries() != null ? reqOpts.getMaxRetries() : this.options.getMaxRetries();
    int maxRetries =
        reqOpts.getMaxRetries() != null ? reqOpts.getMaxRetries() : this.options.getMaxRetries();
    FinalRequestOptions.HTTPMethod method =
        reqOpts.getMethod() != null ? reqOpts.getMethod() : FinalRequestOptions.HTTPMethod.GET;
    String url = buildUrl(reqOpts.getPath(), reqOpts.getQueries());
    String body = buildBody(reqOpts.getBody());
    java.util.Map<String, String> headers = buildHeaders(reqOpts.getHeaders());

    // Use per-request timeout if specified, otherwise use default httpClient
    OkHttpClient clientToUse = httpClient;
    if (reqOpts.getTimeout() != null && reqOpts.getTimeout() != httpClient.connectTimeoutMillis()) {
      clientToUse =
          httpClient
              .newBuilder()
              .connectTimeout(reqOpts.getTimeout(), TimeUnit.MILLISECONDS)
              .readTimeout(reqOpts.getTimeout(), TimeUnit.MILLISECONDS)
              .writeTimeout(reqOpts.getTimeout(), TimeUnit.MILLISECONDS)
              .build();
    }

    while (true) {
      Request req = buildRequest(method, url, body, headers);
      try (Response res = clientToUse.newCall(req).execute()) {
        int status = res.code();
        if (status < 200 || status >= 300) {
          if (retriesRemaining > 0 && shouldRetry(status)) {
            retriesRemaining -= 1;
            sleep(
                computeRetryDelayMs(
                    res, maxRetries - retriesRemaining, maxRetries, retriesRemaining));
            continue;
          }
          String text = "";
          if (res.body() != null) {
            text = res.body().string();
          }
          throw createAPIException(text, status);
        }

        return processFileDownloadResponse(res);
      } catch (IOException e) {
        if (retriesRemaining > 0) {
          retriesRemaining -= 1;
          sleep(computeBackoffMs(maxRetries - retriesRemaining));
          continue;
        }
        throw new ConnectionException(e.getMessage(), e);
      }
    }
  }

  /** Close */
  public void close() {
    options.close();
  }

  /**
   * PaymentRequestsService
   *
   * @return PaymentRequestsService
   */
  public PaymentRequestsService paymentRequests() {
    return new PaymentRequestsServiceImpl(this);
  }

  /**
   * WebhooksService
   *
   * @return WebhooksService
   */
  public WebhooksService webhooks() {
    return new WebhooksServiceImpl(this);
  }

  /**
   * PayoutsService
   *
   * @return PayoutsService
   */
  public PayoutsService payouts() {
    return new PayoutsServiceImpl(this);
  }

  /**
   * PayoutsAccountService
   *
   * @return PayoutsAccountService
   */
  public PayoutsAccountService payoutsAccount() {
    return new PayoutsAccountServiceImpl(this);
  }
}
