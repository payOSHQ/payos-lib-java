package vn.payos.examples;

import vn.payos.PayOS;
import vn.payos.exception.APIException;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;

public class BasicUsageExample {
  public static void main(String[] args) {
    PayOS client = new PayOS("PAYOS_CLIENT_ID", "PAYOS_API_KEY", "PAYOS_CHECKSUM_KEY");
    Long orderCode = System.currentTimeMillis() / 1000;
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
    try {
      CreatePaymentLinkResponse data = client.paymentRequests().create(paymentData);
      System.out.println(data.toString());

    } catch (APIException e) {
      System.out.println(e.getErrorDesc().get());
    }
  }
}
