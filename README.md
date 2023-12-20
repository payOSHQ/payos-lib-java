The payOS library provides convenient access to the payOS API from applications written in server-side.

## Documentation
See the [payOS API docs](https://payos.vn/docs/api/) for more infomation.

## Installation
Download file out/artifacts/payos_jar/payos.jar

Import library on your application Maven.
- **IntelliJ IDEA**:
  - Move file payos.jar to ${project.basedir}/libs/payos.jar
  - Add config in file pom.xml
    ```
      <dependency>
          <groupId>io.github</groupId>
          <artifactId>payoshq</artifactId>
          <version>0.0.1</version>
          <scope>system</scope>
          <systemPath>${project.basedir}/libs/payos.jar</systemPath>
        </dependency>
    ```
  - Load library:
    ```bash
      mvn install
    ```
## Usage
### Initialize
You need to initialize the PayOS object with the Client ID, Api Key and Checksum Key of the payment channel you created.

* CommonJava
```java
    import com.lib.payos.PayOS;
    
    public class SpringbootBackendPayosApplication{
      public static void main(String[] args) {
        PayOS payOS = new PayOS(clientId, apiKey, checksumKey);
      }
    }


```


### Methods included in the PayOS object

* **createPaymentLink**

Create a payment link for the order data

Syntax:
```
    JsonNode data = payOS.createPaymentLink(paymentData);
```
Parameter data type: attribute assign null meaning it is not required
```java
//PaymentData Type
    @Getter
    @Setter
    public class PaymentData {
      private int orderCode;
      private int amount;
      private String description;
      private List<ItemData> items;
      private String cancelUrl;
      private String returnUrl;
      private String signature = null;
      private String buyerName = null;
      private String buyerEmail = null;
      private String buyerPhone = null;
      private String buyerAddress= null;
      private Integer expiredAt = null;
      public PaymentData(int orderCode, int amount, String description, List<ItemData> items,
          String cancelUrl, String returnUrl) {
        this.orderCode = orderCode;
        this.amount = amount;
        this.description = description;
        this.items = items;
        this.cancelUrl = cancelUrl;
        this.returnUrl = returnUrl;
      }
    }
    //ItemData Type
    
    @AllArgsConstructor
    @Getter
    @Setter
    public class ItemData {
      private String name;
      private int quantity;
      private int price;
    }

```
Note that: If you want to assign field expiredAt = 1, you can do this by set method. Example
```
    paymentData.setExpiredAt(1);
```
Return data type: JsonNode has some attribute
```
    {
      "bin": String;
      "accountNumber": String;
      "accountName": String;
      "amount": int;
      "description": String;
      "orderCode": int;
      "paymentLinkId": String;
      "status": String;
      "checkoutUrl": String;
      "qrCode": String
    }
```
Examples:
```java
    import com.lib.payos.PayOS;
    import com.lib.payos.type.PaymentData;
    import com.lib.payos.type.ItemData;
    public class SpringbootBackendPayosApplication{
      public static void main(String[] args) {
        PayOS payOS = new PayOS(clientId, apiKey, checksumKey);
        
    //    Create new Item and add to the list 
        ItemData item = new ItemData("Mì tôm hảo hảo Ly", 1, 1000);
        List<ItemData> itemList = new ArrayList<ItemData>();
        itemList.add(item);
        //Create new PaymentData
        PaymentData paymentData = new PaymentData(orderCode, price, description,
            itemList, cancelUrl, returnUrl);
    
        JsonNode data = payOS.createPaymentLink(paymentData);
      }
    }

```
* **getPaymentLinkInfomation**

Get payment information of an order that has created a payment link.

Syntax:
```
    JsonNode res = payOS.getPaymentLinkInfomation(id);
```

Parameters:
* `id`: Store order code (`orderCode`) or payOS payment link id (`paymentLinkId`). Type of `id` is int.


Return data type: JsonNode has some field:
```
{
  "id": int;
  "orderCode": int;
  "amount": int;
  "amountPaid": int;
  "amountRemaining": int;
  "status": String;
  "createdAt": String;
  "transactions": JsonNode;
  "cancellationReason": String | Null;
  "canceledAt": String | Null;
}
```

Transaction type: JsonNode
```
    {
      "reference": String;
      "amount": int;
      "accountNumber": String;
      "description": String;
      "transactionDateTime": String;
      "virtualAccountName": String | Null;
      "virtualAccountNumber": String | Null;
      "counterAccountBankId": String | Null;
      "counterAccountBankName": String | Null;
      "counterAccountName": String | Null;
      "counterAccountNumber": String | Null
    }
```
Example:
```java
    JsonNode paymentLinkInfo = payOS.getPaymentLinkInfomation(1234)
```

* **cancelPaymentLink**

Cancel the payment link of the order.

Syntax:
```java
    JsonNode order = payOS.cancelPaymentLink(orderId, null);
```

Parameters:
* `id`: Store order code (`orderCode`) or payOS payment link id (`paymentLinkId`). Type of `id` is str or int.

* `cancellationReason`: Reason for canceling payment link (optional).

Return data type: JsonNode with some field
```
    {
      'id': String;
      'orderCode': int;
      'amount': int;
      'amountPaid': int;
      'amountRemaining': int;
      'status': String;
      'createdAt': String;
      'transactions': JsonNode;
      'cancellationReason': String | Null;
      'canceledAt': String | Null;
    }
```
Example:

```java
    int orderCode = 123;
    String cancellationReason = "reason";
    
    JsonNode cancelledPaymentLinkInfo = payOS.cancelPaymentLink(orderCode, cancellationReason); 

    // If you want to cancel the payment link without reason:
    JsonNode cancelledPaymentLinkInfo = payOS.cancelPaymentLink(orderCode, null); 
```


* **confirmWebhook**

Validate the Webhook URL of a payment channel and add or update the Webhook URL for that Payment Channel if successful.

Syntax:

```java
    String res = payOS.confirmWebhook("https://your-webhook-url/");
```

* **verifyPaymentWebhookData**

Verify data received via webhook after payment.

Syntax:

```Java
    
    JsonNode paymentData = payOS.verifyPaymentWebhookData(webhookUrl)
```

Return data type: JsonNode with some field
```Java
    {
      'orderCode': int;
      'amount': int;
      'description': String;
      'accountNumber': String;
      'reference': String;
      'transactionDateTime': String;
      'paymentLinkId': String;
      'code': String;
      'desc': String;
      'counterAccountBankId': String | Null;
      'counterAccountBankName': String | Null;
      'counterAccountName': String | Null;
      'counterAccountNumber': String | Null;
      'virtualAccountName': String | Null;
      'virtualAccountNumber': String | Null;
    }
```

