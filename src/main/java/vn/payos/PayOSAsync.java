package vn.payos;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
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
import vn.payos.service.async.v1.payouts.PayoutsService;
import vn.payos.service.async.v1.payouts.PayoutsServiceImpl;
import vn.payos.service.async.v1.payoutsAccount.PayoutsAccountService;
import vn.payos.service.async.v1.payoutsAccount.PayoutsAccountServiceImpl;
import vn.payos.service.async.v2.paymentRequests.PaymentRequestsService;
import vn.payos.service.async.v2.paymentRequests.PaymentRequestsServiceImpl;
import vn.payos.service.async.webhook.WebhooksService;
import vn.payos.service.async.webhook.WebhooksServiceImpl;

/** payOS async client */
public class PayOSAsync extends Client {

  /**
   * payOS async client
   *
   * @param clientId client id
   * @param apiKey api key
   * @param checksumKey checksum key
   */
  public PayOSAsync(@NonNull String clientId, @NonNull String apiKey, @NonNull String checksumKey) {
    super(clientId, apiKey, checksumKey);
  }

  /**
   * payOS async client
   *
   * @param clientId client id
   * @param apiKey api key
   * @param checksumKey checksum key
   * @param partnerCode partner code
   */
  public PayOSAsync(
      @NonNull String clientId,
      @NonNull String apiKey,
      @NonNull String checksumKey,
      String partnerCode) {
    super(clientId, apiKey, checksumKey, partnerCode);
  }

  /**
   * payOS async client
   *
   * @param options options
   */
  public PayOSAsync(@NonNull ClientOptions options) {
    super(options);
  }

  /**
   * payOS async client
   *
   * @return payOS async client
   */
  public static PayOSAsync fromEnv() {
    return new PayOSAsync(ClientOptions.fromEnv());
  }

  /**
   * payOS client
   *
   * @return payOS client
   */
  public PayOS sync() {
    return new PayOS(this.options);
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
  public <TResp, TReq> CompletableFuture<TResp> requestAsync(
      FinalRequestOptions<TReq> options, Class<TResp> dataClass) {
    return executeAsync(options, dataClass);
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
  public <TResp> CompletableFuture<TResp> getAsync(
      String path, Class<TResp> dataClass, RequestOptions<Void> options) {
    FinalRequestOptions<Void> finalOpts =
        FinalRequestOptions.fromRequestOptions(HTTPMethod.GET, path, options);
    return requestAsync(finalOpts, dataClass);
  }

  /**
   * Make GET request
   *
   * @param <TResp> response body type
   * @param path path
   * @param dataClass response data class
   * @return response
   */
  public <TResp> CompletableFuture<TResp> getAsync(String path, Class<TResp> dataClass) {
    FinalRequestOptions<?> finalOpts =
        FinalRequestOptions.builder().path(path).method(HTTPMethod.GET).build();
    return requestAsync(finalOpts, dataClass);
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
  public <TResp, TReq> CompletableFuture<TResp> postAsync(
      String path, Class<TResp> dataClass, RequestOptions<TReq> options) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.fromRequestOptions((HTTPMethod.POST), path, options);
    return requestAsync(finalOpts, dataClass);
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
  public <TResp, TReq> CompletableFuture<TResp> postAsync(String path, Class<TResp> dataClass) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.<TReq>builder().path(path).method(HTTPMethod.POST).build();
    return requestAsync(finalOpts, dataClass);
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
  public <TResp, TReq> CompletableFuture<TResp> putAsync(
      String path, Class<TResp> dataClass, RequestOptions<TReq> options) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.fromRequestOptions((HTTPMethod.PUT), path, options);
    return requestAsync(finalOpts, dataClass);
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
  public <TResp, TReq> CompletableFuture<TResp> putAsync(String path, Class<TResp> dataClass) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.<TReq>builder().path(path).method(HTTPMethod.PUT).build();
    return requestAsync(finalOpts, dataClass);
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
  public <TResp, TReq> CompletableFuture<TResp> patchAsync(
      String path, Class<TResp> dataClass, RequestOptions<TReq> options) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.fromRequestOptions((HTTPMethod.PATCH), path, options);
    return requestAsync(finalOpts, dataClass);
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
  public <TResp, TReq> CompletableFuture<TResp> patchAsync(String path, Class<TResp> dataClass) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.<TReq>builder().path(path).method(HTTPMethod.PATCH).build();
    return requestAsync(finalOpts, dataClass);
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
  public <TResp, TReq> CompletableFuture<TResp> deleteAsync(
      String path, Class<TResp> dataClass, RequestOptions<TReq> options) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.fromRequestOptions((HTTPMethod.DELETE), path, options);
    return requestAsync(finalOpts, dataClass);
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
  public <TResp, TReq> CompletableFuture<TResp> deleteAsync(String path, Class<TResp> dataClass) {
    FinalRequestOptions<TReq> finalOpts =
        FinalRequestOptions.<TReq>builder().path(path).method(HTTPMethod.DELETE).build();
    return requestAsync(finalOpts, dataClass);
  }

  private <TResp, TReq> CompletableFuture<TResp> executeAsync(
      FinalRequestOptions<TReq> reqOpts, Class<TResp> dataClass) {
    return executeAsync(
        reqOpts,
        dataClass,
        reqOpts.getMaxRetries() != null ? reqOpts.getMaxRetries() : this.options.getMaxRetries());
  }

  private <TResp, TReq> CompletableFuture<TResp> executeAsync(
      FinalRequestOptions<TReq> reqOpts, Class<TResp> dataClass, int retriesRemaining) {

    CompletableFuture<TResp> future = new CompletableFuture<>();
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

    applySignatureHeaders(headers, reqOpts.getSignatureOpts(), reqOpts.getBody());
    Request req = buildRequest(method, url, body, headers);

    clientToUse
        .newCall(req)
        .enqueue(
            new Callback() {
              @Override
              public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (retriesRemaining > 0) {
                  options
                      .getStreamHandlerExecutor()
                      .schedule(
                          () -> {
                            executeAsync(reqOpts, dataClass, retriesRemaining - 1)
                                .whenComplete(
                                    (result, error) -> {
                                      if (error != null) {
                                        future.completeExceptionally(error);
                                      } else {
                                        future.complete(result);
                                      }
                                    });
                          },
                          computeBackoffMs(maxRetries - retriesRemaining),
                          TimeUnit.MILLISECONDS);
                } else {
                  future.completeExceptionally(new ConnectionException(e.getMessage(), e));
                }
              }

              @Override
              public void onResponse(@NonNull Call call, @NonNull Response response)
                  throws IOException {
                try {
                  int status = response.code();
                  String text = "";
                  if (response.body() != null) {
                    text = response.body().string();
                  }

                  if (status < 200 || status >= 300) {
                    if (retriesRemaining > 0 && shouldRetry(status)) {
                      options
                          .getStreamHandlerExecutor()
                          .schedule(
                              () -> {
                                executeAsync(reqOpts, dataClass, retriesRemaining - 1)
                                    .whenComplete(
                                        (result, error) -> {
                                          if (error != null) {
                                            future.completeExceptionally(error);
                                          } else {
                                            future.complete(result);
                                          }
                                        });
                              },
                              computeRetryDelayMs(
                                  response,
                                  maxRetries - retriesRemaining,
                                  maxRetries,
                                  retriesRemaining),
                              TimeUnit.MILLISECONDS);
                      return;
                    }
                    future.completeExceptionally(createAPIException(text, status));
                    return;
                  }

                  TResp result = processResponse(text, status, response, reqOpts, dataClass);
                  future.complete(result);
                } catch (Exception e) {
                  future.completeExceptionally(e);
                } finally {
                  response.close();
                }
              }
            });

    return future;
  }

  /**
   * Download file
   *
   * @param <TReq> request body type
   * @param reqOpts options
   * @return File information
   */
  public <TReq> CompletableFuture<FileDownloadResponse> downloadFileAsync(
      FinalRequestOptions<TReq> reqOpts) {
    return downloadFileAsync(
        reqOpts,
        reqOpts.getMaxRetries() != null ? reqOpts.getMaxRetries() : this.options.getMaxRetries());
  }

  private <TReq> CompletableFuture<FileDownloadResponse> downloadFileAsync(
      FinalRequestOptions<TReq> reqOpts, int retriesRemaining) {

    CompletableFuture<FileDownloadResponse> future = new CompletableFuture<>();
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

    Request req = buildRequest(method, url, body, headers);

    clientToUse
        .newCall(req)
        .enqueue(
            new Callback() {
              @Override
              public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (retriesRemaining > 0) {
                  long delayMs = computeBackoffMs(maxRetries - retriesRemaining);
                  options
                      .getStreamHandlerExecutor()
                      .schedule(
                          () -> {
                            downloadFileAsync(reqOpts, retriesRemaining - 1)
                                .whenComplete(
                                    (result, error) -> {
                                      if (error != null) {
                                        future.completeExceptionally(error);
                                      } else {
                                        future.complete(result);
                                      }
                                    });
                          },
                          delayMs,
                          TimeUnit.MILLISECONDS);
                } else {
                  future.completeExceptionally(new ConnectionException(e.getMessage(), e));
                }
              }

              @Override
              public void onResponse(@NonNull Call call, @NonNull Response response)
                  throws IOException {
                try {
                  int status = response.code();

                  if (status < 200 || status >= 300) {
                    if (retriesRemaining > 0 && shouldRetry(status)) {
                      long delayMs =
                          computeRetryDelayMs(
                              response,
                              maxRetries - retriesRemaining,
                              maxRetries,
                              retriesRemaining);
                      options
                          .getStreamHandlerExecutor()
                          .schedule(
                              () -> {
                                downloadFileAsync(reqOpts, retriesRemaining - 1)
                                    .whenComplete(
                                        (result, error) -> {
                                          if (error != null) {
                                            future.completeExceptionally(error);
                                          } else {
                                            future.complete(result);
                                          }
                                        });
                              },
                              delayMs,
                              TimeUnit.MILLISECONDS);
                      return;
                    }
                    String text = "";
                    if (response.body() != null) {
                      text = response.body().string();
                    }
                    future.completeExceptionally(createAPIException(text, status));
                    return;
                  }

                  FileDownloadResponse result = processFileDownloadResponse(response);
                  future.complete(result);
                } catch (Exception e) {
                  future.completeExceptionally(e);
                } finally {
                  response.close();
                }
              }
            });

    return future;
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
