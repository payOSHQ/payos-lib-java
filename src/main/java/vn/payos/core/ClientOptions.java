package vn.payos.core;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/** ClientOptions */
@Getter
@Builder
@AllArgsConstructor
public class ClientOptions {
  static final String DEFAULT_BASE_URL = "https://api-merchant.payos.vn";

  /** LogLevel */
  public enum LogLevel {
    /** NONE */
    NONE,
    /** INFO */
    INFO,
    /** DEBUG */
    DEBUG
  }

  @NonNull private final String clientId;

  @NonNull private final String apiKey;

  @NonNull private final String checksumKey;

  private final String partnerCode;

  @Builder.Default private final String baseURL = DEFAULT_BASE_URL;

  @Builder.Default private final int timeoutMs = 60_000;

  @Builder.Default private final int maxRetries = 2;

  @Builder.Default private final LogLevel logLevel = LogLevel.NONE;

  private final OkHttpClient httpClient;

  private final ScheduledExecutorService streamHandlerExecutor;

  /**
   * Get httpClient
   *
   * @return httpClient
   */
  public OkHttpClient getHttpClient() {
    if (httpClient != null) {
      return httpClient;
    }

    OkHttpClient.Builder builder =
        new OkHttpClient.Builder()
            .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .writeTimeout(timeoutMs, TimeUnit.MILLISECONDS);

    if (logLevel != LogLevel.NONE) {
      HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

      switch (logLevel) {
        case DEBUG:
          loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
          break;
        case INFO:
          loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
          break;
        default:
          loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
          break;
      }

      loggingInterceptor.redactHeader("Authorization");
      loggingInterceptor.redactHeader("Cookie");
      loggingInterceptor.redactHeader("Set-Cookie");
      loggingInterceptor.redactHeader("x-client-id");
      loggingInterceptor.redactHeader("x-api-key");

      builder.addInterceptor(loggingInterceptor);
    }

    return builder.build();
  }

  /**
   * Get streamHandlerExecutor
   *
   * @return streamHandlerExecutor
   */
  public ScheduledExecutorService getStreamHandlerExecutor() {
    if (streamHandlerExecutor != null) {
      return streamHandlerExecutor;
    }
    return Executors.newScheduledThreadPool(2);
  }

  /** close */
  public void close() {
    if (httpClient != null) {
      httpClient.dispatcher().executorService().shutdown();
      httpClient.connectionPool().evictAll();
      if (httpClient.cache() != null) {
        try {
          httpClient.cache().close();
        } catch (IOException ignored) {
        }
      }
    }
    if (streamHandlerExecutor != null) {
      streamHandlerExecutor.shutdown();
    }
  }

  /**
   * ClientOptions from env
   *
   * @return ClientOptions
   */
  public static ClientOptions fromEnv() {
    String clientId = getConfigValue("payos.client-id", "PAYOS_CLIENT_ID");
    String apiKey = getConfigValue("payos.api-key", "PAYOS_API_KEY");
    String checksumKey = getConfigValue("payos.checksum-key", "PAYOS_CHECKSUM_KEY");

    if (clientId == null || clientId.trim().isEmpty()) {
      throw new IllegalArgumentException(
          "Client ID is required. Set PAYOS_CLIENT_ID environment variable or payos.client-id"
              + " system property.");
    }
    if (apiKey == null || apiKey.trim().isEmpty()) {
      throw new IllegalArgumentException(
          "API Key is required. Set PAYOS_API_KEY environment variable or payos.api-key system"
              + " property.");
    }
    if (checksumKey == null || checksumKey.trim().isEmpty()) {
      throw new IllegalArgumentException(
          "Checksum Key is required. Set PAYOS_CHECKSUM_KEY environment variable or"
              + " payos.checksum-key system property.");
    }

    String partnerCode = getConfigValue("payos.partner-code", "PAYOS_PARTNER_CODE");
    String baseURL = getConfigValue("payos.base-url", "PAYOS_BASE_URL");
    if (baseURL == null || baseURL.trim().isEmpty()) {
      baseURL = DEFAULT_BASE_URL;
    }

    int timeout = parseIntConfig("payos.timeout-ms", "PAYOS_TIMEOUT_MS", 60_000);
    int retries = parseIntConfig("payos.max-retries", "PAYOS_MAX_RETRIES", 2);

    if (timeout <= 0) {
      throw new IllegalArgumentException("Timeout must be positive, got: " + timeout);
    }
    if (retries < 0) {
      throw new IllegalArgumentException("Max retries must be non-negative, got: " + retries);
    }

    LogLevel logLevel = parseLogLevelConfig("payos.log-level", "PAYOS_LOG_LEVEL", LogLevel.NONE);

    return ClientOptions.builder()
        .clientId(clientId.trim())
        .apiKey(apiKey.trim())
        .checksumKey(checksumKey.trim())
        .partnerCode(partnerCode != null ? partnerCode.trim() : null)
        .baseURL(baseURL.trim())
        .timeoutMs(timeout)
        .maxRetries(retries)
        .logLevel(logLevel)
        .httpClient(null)
        .streamHandlerExecutor(null)
        .build();
  }

  private static String getConfigValue(String systemProperty, String envVariable) {
    String value = System.getProperty(systemProperty);
    if (value == null || value.isEmpty()) {
      value = System.getenv(envVariable);
    }
    return value;
  }

  private static int parseIntConfig(String systemProperty, String envVariable, int defaultValue) {
    String value = getConfigValue(systemProperty, envVariable);
    if (value != null && !value.trim().isEmpty()) {
      try {
        return Integer.parseInt(value.trim());
      } catch (NumberFormatException e) {
        // ignore, use default value
      }
    }
    return defaultValue;
  }

  private static LogLevel parseLogLevelConfig(
      String systemProperty, String envVariable, LogLevel defaultValue) {
    String value = getConfigValue(systemProperty, envVariable);
    if (value != null && !value.trim().isEmpty()) {
      try {
        return LogLevel.valueOf(value.trim().toUpperCase());
      } catch (IllegalArgumentException e) {
        // ignore, use default value
      }
    }
    return defaultValue;
  }

  /**
   * ClientOptions
   *
   * @param clientId client id
   * @param apiKey api key
   * @param checksumKey checksum key
   */
  public ClientOptions(
      @NonNull String clientId, @NonNull String apiKey, @NonNull String checksumKey) {
    this(
        clientId,
        apiKey,
        checksumKey,
        null,
        DEFAULT_BASE_URL,
        60_000,
        2,
        LogLevel.NONE,
        null,
        null);
  }

  /**
   * ClientOptions
   *
   * @param clientId client id
   * @param apiKey api key
   * @param checksumKey checksum key
   * @param partnerCode partner code
   */
  public ClientOptions(
      @NonNull String clientId,
      @NonNull String apiKey,
      @NonNull String checksumKey,
      String partnerCode) {
    this(
        clientId,
        apiKey,
        checksumKey,
        partnerCode,
        DEFAULT_BASE_URL,
        60_000,
        2,
        LogLevel.NONE,
        null,
        null);
  }
}
