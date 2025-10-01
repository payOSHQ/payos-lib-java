# Migration guide

This guild outline changes and step needed to migrate your codebase to the lasted version of payOS Java SDK.

## Breaking changes

### Methods and types name

All methods payment-requests now accessed by `.paymentRequests()` method, some type name has been changed.

```java
// before
ItemData itemData =
    ItemData.builder().name("Mỳ tôm Hảo Hảo ly").quantity(1).price(2000).build();
PaymentData paymentData =
    PaymentData.builder()
        .orderCode(orderCode)
        .amount(2000)
        .description("Thanh toan")
        .returnUrl("https://your-url.com/success")
        .cancelUrl("https://your-url.com/cancel")
        .item(itemData)
        .build();
CheckoutResponseData data = client.createPaymentLink(paymentData);

// after
PaymentLinkItem itemData =
    PaymentLinkItem.builder().name("Mỳ tôm Hảo Hảo ly").quantity(1).price(2000L).build();
CreatePaymentLinkRequest paymentData =
    CreatePaymentLinkRequest.builder()
        .orderCode(orderCode)
        .amount(2000L)
        .description("Thanh toan")
        .returnUrl("https://your-url.com/success")
        .cancelUrl("https://your-url.com/cancel")
        .item(itemData)
        .build();
CreatePaymentLinkResponse data = client.paymentRequests().create(paymentData);
```

```java
// before
PaymentLinkData paymentLink = client.getPaymentLinkInformation(orderCode);

// after
PaymentLink paymentLink = client.paymentRequests().get(orderCode);
```

```java
// before
PaymentLinkData paymentLink = client.cancelPaymentLink(orderCode, cancellationReason);

// after
PaymentLink paymentLink = client.paymentRequests().cancel(orderCode, cancellationReason);
```

All webhook methods now accessed by `.webhooks()` method.

```java
// before
String result = client.confirmWebhook(webhookUrl);

// after
ConfirmWebhookResponse result = client.webhooks().confirm(webhookUrl);
```

```java
// before
WebhookData result = client.verifyPaymentWebhookData(webhookBody);

// after
WebhookData result = client.webhooks().verify(webhookBody);
```

### Handling errors

The SDK will throw `APIException` for API exception, `WebhookException` for webhook exception, `InvalidSignatureException` for signature exception, all those exception is subclass of `PayOSException`.

```java
// before
try {
    ItemData itemData =
        ItemData.builder().name("Mỳ tôm Hảo Hảo ly").quantity(1).price(2000).build();
    PaymentData paymentData =
        PaymentData.builder()
            .orderCode(orderCode)
            .amount(2000)
            .description("Thanh toan")
            .returnUrl("https://your-url.com/success")
            .cancelUrl("https://your-url.com/cancel")
            .item(itemData)
            .build();
    CheckoutResponseData data = client.createPaymentLink(paymentData);
} catch (PayOSException e) {
    // TODO: handle exception of payOS
} catch (Exception e) {
    // TODO: handle exception
}

// after
try {
    PaymentLinkItem itemData =
        PaymentLinkItem.builder().name("Mỳ tôm Hảo Hảo ly").quantity(1).price(2000L).build();
    CreatePaymentLinkRequest paymentData =
        CreatePaymentLinkRequest.builder()
            .orderCode(orderCode)
            .amount(2000L)
            .description("Thanh toan")
            .returnUrl("https://your-url.com/success")
            .cancelUrl("https://your-url.com/cancel")
            .item(itemData)
            .build();
    CreatePaymentLinkResponse data = client.paymentRequests().create(paymentData);
} catch (APIException e) {
    // TODO: handle exception for api error
} catch (PayOSException e) {
    // TODO: handle other exception of payOS
} catch (Exception e) {
    // TODO: handle exception
}
```
