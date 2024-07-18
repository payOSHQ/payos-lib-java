The payOS library provides convenient access to the payOS API from applications written in server-side.

## Documentation

See the [payOS API docs](https://payos.vn/docs/api/) for more information.

## Installation

Import library on your application Maven.

- Move file payos-java-1.0.0.jar to ${project.basedir}/libs/payos-java-1.0.0.jar
- Add config in file pom.xml
  ```
  <dependency>
    <groupId>vn.payos</groupId>
    <artifactId>payos-java</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/payos-java-1.0.0.jar</systemPath>
  </dependency>
  ```
- Load library:
  ```bash
    mvn install
  ```

## Usage

### Initialize

You need to initialize the PayOS object with the Client ID, Api Key and Checksum Key of the payment channel you created. Your Partner Code is optional.

```java
import vn.payos.PayOS;

public void main(String[] args) {
  String clientId = "your-client-id";
  String apiKey = "your-api-key";
  String checksumKey = "your-checksum-key";
  String partnerCode = "your-partner-code";

  PayOS payOS = new PayOS(clientId, apiKey, checksumKey);

  // or with partnerCode

  PayOS payOS = new PayOS(clientId, apiKey, checksumKey, partnerCode);

}

```

### Methods included in the PayOS object

- **createPaymentLink**

Create a payment link for the order data

Syntax:

```java
CheckoutResponseData data = payOS.createPaymentLink(paymentData);
```

Parameter data type: PaymentData

```java
ItemData itemData = ItemData.builder().name("Mỳ tôm Hảo Hảo ly").quantity(1).price(2000).build();

PaymentData paymentData = PaymentData.builder()
    .orderCode(orderCode).amount(2000)
    .description("Thanh toán đơn hàng")
    .returnUrl(webhookUrl + "/success")
    .cancelUrl(webhookUrl + "/cancel")
    .item(itemData).build();

```

Note that:

- If you want to assign field, you can do this by set method. Example:

```java
paymentData.setExpiredAt(1);
```

- If you want to add more item to the list, you can do this by add method. Example:

```java
paymentData.addItem(itemData);
```

Return data type: CheckoutResponseData

```java
public class CheckoutResponseData {
  private String bin;
  private String accountNumber;
  private String accountName;
  private Integer amount;
  private String description;
  private Long orderCode;
  private String currency;
  private String paymentLinkId;
  private String status;
  private String checkoutUrl;
  private String qrCode;
}
```

- **getPaymentLinkInformation**

Get payment information of an order that has created a payment link.

Syntax:

```java
PaymentLinkData paymentData = payOS.getPaymentLinkInformation(orderCode);
```

Parameters:

- `id`: Store order code (`orderCode`) or payOS payment link id (`paymentLinkId`). Type of `id` is long.

Return data type: PaymentLinkData

```java
public class PaymentLinkData {
  private String id;
  private Long orderCode;
  private Integer amount;
  private Integer amountPaid;
  private Integer amountRemaining;
  private String status;
  private String createdAt;
  private List<Transaction> transactions;
  private String cancellationReason;
  private String canceledAt;
}
```

Transaction type: Transaction

```java
public class Transaction {
  private String reference;
  private Integer amount;
  private String accountNumber;
  private String description;
  private String transactionDateTime;
  private String virtualAccountName;
  private String virtualAccountNumber;
  private String counterAccountBankId;
  private String counterAccountBankName;
  private String counterAccountName;
  private String counterAccountNumber;
}
```

- **cancelPaymentLink**

Cancel the payment link of the order.

Syntax:

```java
PaymentLinkData paymentData = payOS.cancelPaymentLink(orderCode, cancellationReason);
```

Parameters:

- `id`: Store order code (`orderCode`) or payOS payment link id (`paymentLinkId`). Type of `id` is long.

- `cancellationReason`: Reason for canceling payment link (optional).

Return data type: PaymentLinkData

```java
public class PaymentLinkData {
  private String id;
  private Long orderCode;
  private Integer amount;
  private Integer amountPaid;
  private Integer amountRemaining;
  private String status;
  private String createdAt;
  private List<Transaction> transactions;
  private String cancellationReason;
  private String canceledAt;
}
```

- **confirmWebhook**

Validate the Webhook URL of a payment channel and add or update the Webhook URL for that Payment Channel if successful.

Syntax:

```java
String result = payOS.confirmWebhook(webhookUrl);
```

- **verifyPaymentWebhookData**

Verify data received via webhook after payment.

Syntax:

```java
WebhookData result = payOS.verifyPaymentWebhookData(webhookBody);
```

Return data type: WebhookData

```java
public class WebhookData {
  private Long orderCode;
  private Integer amount;
  private String description;
  private String accountNumber;
  private String reference;
  private String transactionDateTime;
  private String currency;
  private String paymentLinkId;
  private String code;
  private String desc;
  private String counterAccountBankId;
  private String counterAccountBankName;
  private String counterAccountName;
  private String counterAccountNumber;
  private String virtualAccountName;
  private String virtualAccountNumber;
}
```

## Development

Java 8 or later is required to build the PayOS Java library. The library uses the latest stable version of the PayOS API. Tests use the same Java runtime as the build.

To run all tests:

```bash
mvn test
```

You can run particular tests by specifying the test class name. For example:

```bash
mvn test -Dtest=vn.payos.PayOSTest
mvn test -Dtest=vn.payos.SignatureTest
```

Build from source:

```bash
mvn install
```
