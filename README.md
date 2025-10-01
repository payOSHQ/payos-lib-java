# payOS Java Library

[![Maven Central](https://img.shields.io/maven-central/v/vn.payos/payos-java)](https://central.sonatype.com/artifact/vn.payos/payos-java)
[![javadoc](https://javadoc.io/badge2/vn.payos/payos-java/javadoc.svg)](https://javadoc.io/doc/vn.payos/payos-java)

The payOS Java library provides convenient access to the payOS Merchant API from applications written in Java.

To learn how to use payOS Merchant API, checkout our [API Reference](https://payos.vn/docs/api) and [Documentation](https://payos.vn/docs). We also have some examples in [Examples](./src/main/java/vn/payos/examples/). Javadocs are available on [javadoc.io](https://javadoc.io/doc/vn.payos/payos-java).

## Requirements

Java 8 or later.

## Installation

Gradle

```groovy
implementation 'vn.payos:payos-java:2.0.0-SNAPSHOT'
```

Maven

```xml
<dependency>
    <groupId>vn.payos</groupId>
    <artifactId>payos-java</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```

## Usage

### Basic usage

An example to create a payment link.

```java
package vn.payos.examples;

import vn.payos.PayOS;
import vn.payos.exception.PayOSException;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

public class BasicUsageExample {
  public static void main(String[] args) {
    // Configures using the `PAYOS_CLIENT_ID`, `PAYOS_API_KEY`, `PAYOS_CHECKSUM_KEY` environment
    // variables
    PayOS client = PayOS.fromEnv();
    CreatePaymentLinkRequest paymentData =
        CreatePaymentLinkRequest.builder()
            .orderCode(System.currentTimeMillis() / 1000)
            .amount(2000L)
            .description("Thanh toan")
            .returnUrl("https://your-url.com/success")
            .cancelUrl("https://your-url.com/cancel")
            .build();

    try {
      CreatePaymentLinkResponse response = client.paymentRequests().create(paymentData);
      System.out.println(response);
    } catch (PayOSException e) {
      e.printStackTrace();
    }
  }
}
```

### Webhook verification

You can register an endpoint to receive the payment webhook.

```java
ConfirmWebhookResponse result = client.webhooks().confirm("https://your-url.com/payos-webhook");
```

Then using `.webhooks().verify()` to verify an receive webhook data.

```java
WebhookData data = client.webhooks().verify(body);
```

For more information about webhooks, see [the API doc](https://payos.vn/docs/api/#tag/payment-webhook/operation/payment-webhook).

### Handling errors

When the API return a non-success status code (i.e, 4xx or 5xx response) or non-success code data (any code except '00'), an exception `APIException` or its subclass will be thrown:

```java
try {
  client.get("/not-found", Object.class);
} catch (APIException e) {
  e.printStackTrace();
}
```

### Asynchronous usage

The default client is synchronous. To switch to asynchronous, call the `async()` method or create an asynchronous client from the beginning.

```java
import java.util.concurrent.CompletableFuture;
import vn.payos.PayOS;
import vn.payos.PayOSAsync;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

public class BasicUsageExample {
  public static void main(String[] args) {
    CreatePaymentLinkRequest paymentData =
        CreatePaymentLinkRequest.builder()
            .orderCode(System.currentTimeMillis() / 1000)
            .amount(2000L)
            .description("Thanh toan")
            .returnUrl("https://your-url.com/success")
            .cancelUrl("https://your-url.com/cancel")
            .build();
    // Configures using the `PAYOS_CLIENT_ID`, `PAYOS_API_KEY`, `PAYOS_CHECKSUM_KEY` environment
    // variables
    PayOS client = PayOS.fromEnv();
    PayOSAsync asyncClient = PayOSAsync.fromEnv();

    CompletableFuture<CreatePaymentLinkResponse> future =
        client.async().paymentRequests().create(paymentData);

    CompletableFuture<CreatePaymentLinkResponse> anotherFuture =
        asyncClient.paymentRequests().create(paymentData);
  }
}

```

### Auto pagination

List method in the payOS Merchant API are paginated, the library provides auto-paginating.

```java
Page<Payout> page = client.payouts().list(GetPayoutListParams.builder().limit(50).build());
for (Payout payout : page.autoPager()) {
  System.out.println(payout.getReferenceId());
}
page.autoPager().stream()
    .limit(10)
    .forEach(payout -> System.out.println(payout.getReferenceId()));
;

```

With asynchronous:

```java
CompletableFuture<PageAsync<Payout>> pageFuture =
    client.async().payouts().list(GetPayoutListParams.builder().limit(50).build());
AsyncStreamResponse.Handler<Payout> handler =
    new AsyncStreamResponse.Handler<Payout>() {

      @Override
      public void onNext(Payout item) {
        System.out.println(item.getReferenceId());
      }

      @Override
      public void onComplete(Optional<Throwable> error) {
        if (error.isPresent()) {
          System.out.println("Error occur");
          throw new RuntimeException(error.get());
        }
        System.out.println("Complete");
      }
    };
pageFuture.thenAccept(page -> page.autoPager().subscribe(handler));

pageFuture.thenAccept(
    page ->
        page.autoPager()
            .subscribe(
                payout -> {
                  System.out.println(payout.getReferenceId());
                })
            .onCompleteFuture()
            .whenComplete(
                (unused, error) -> {
                  if (error != null) {
                    System.out.println("Error occur");
                    throw new RuntimeException(error);
                  }
                  System.out.println("Complete");
                }));
```

Alternative, you can use `getItems()`, `hasNextPage()`, `nextPage()` to manually request next page:

```java
Page<Payout> page = client.payouts().list(GetPayoutListParams.builder().limit(50).build());
while (true) {
  for (Payout payout : page.getItems()) {
    System.out.println(payout.getReferenceId());
  }
  if (!page.hasNextPage()) {
    break;
  }
  page = page.nextPage();
}
```

### Advanced usage

#### Custom configuration

You can customize the PayOS client with a various options:

```java
PayOS client =
    new PayOS(
        ClientOptions.builder()
            .clientId("YOUR_CLIENT_ID")
            .apiKey("YOUR_API_KEY")
            .checksumKey("YOUR_CHECKSUM_KEY")
            .baseURL("https://api-merchant.payos.vn")
            .logLevel(LogLevel.DEBUG) // only work for default httpClient
            .maxRetries(1)
            .timeoutMs(50)
            .httpClient(yourHttpClient)
            .build());
```

See this table for the available options with system properties and environment variables:

| Setter        | System property      | Environment variable | Required | Default value                   |
| ------------- | -------------------- | -------------------- | -------- | ------------------------------- |
| `clientId`    | `payos.client-id`    | `PAYOS_CLIENT_ID`    | true     | -                               |
| `apiKey`      | `payos.api-key`      | `PAYOS_API_KEY`      | true     | -                               |
| `checksumKey` | `payos.checksum-key` | `PAYOS_CHECKSUM_KEY` | true     | -                               |
| `partnerCode` | `payos.partner-code` | `PAYOS_PARTNER_CODE` | false    | -                               |
| `baseURL`     | `payos.base-url`     | `PAYOS_BASE_URL`     | false    | `https://api-merchant.payos.vn` |
| `timeoutMs`   | `payos.timeout-ms`   | `PAYOS_TIMEOUT_MS`   | false    | 60000                           |
| `maxRetries`  | `payos.max-retries`  | `PAYOS_MAX_RETRIES`  | false    | 2                               |
| `logLevel`    | `payos.log-level`    | `PAYOS_LOG_LEVEL`    | false    | NONE                            |

#### Request-level options

You can override client-level options for individual request:

```java
PayOS client =
    new PayOS(
        ClientOptions.builder()
            .clientId("YOUR_CLIENT_ID")
            .apiKey("YOUR_API_KEY")
            .checksumKey("YOUR_CHECKSUM_KEY")
            .baseURL("https://api-merchant.payos.vn")
            .logLevel(LogLevel.DEBUG) // only work for default httpClient
            .maxRetries(1)
            .timeoutMs(50)
            .httpClient(yourHttpClient)
            .build());
CompletableFuture<PageAsync<Payout>> response =
    client
        .async()
        .payouts()
        .list(
            GetPayoutListParams.builder().limit(5).build(),
            RequestOptions.builder()
                .maxRetries(0) // `0` mean no retry request
                .timeout(5000)
                .build());
```

#### Signature

The signature can be manually create by `.getCrypto()` method:

```java
// for create payment link signature
String createPaymentLinkSignature =
    client.getCrypto().createSignatureOfPaymentRequest(data, checksumKey);

// for payment-requests and webhook signature
String paymentRequestSignature = client.getCrypto().createSignatureFromObj(data, checksumKey);

// for payouts signature
String payoutsSignature = client.getCrypto().createSignature(checksumKey, data);
```

## Contributing

See [the contributing documentation](./CONTRIBUTING.md).
