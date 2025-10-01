package vn.payos.examples;

import vn.payos.PayOS;
import vn.payos.exception.APIException;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.InvoiceRequest;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.v2.paymentRequests.TaxPercentage;

public class PaymentLinkWithInvoiceExample {
  public static void main(String[] args) {
    PayOS client = new PayOS("YOUR_CLIENT_ID", "YOUR_API_KEY", "YOUR_CHECKSUM_KEY");
    Long orderCode = System.currentTimeMillis() / 1000;
    CreatePaymentLinkRequest paymentData =
        CreatePaymentLinkRequest.builder()
            .orderCode(orderCode)
            .amount(126500L) // total amount including tax
            .description("orderCode" + orderCode)
            .returnUrl("https://your-url.com/success")
            .cancelUrl("https://your-url.com/cancel")

            // buyer information
            .buyerAddress("123 Nguyen Trai, District 1, Ho Chi Minh City")
            .buyerCompanyName("ABC Company Ltd.")
            .buyerEmail("customer@example.com")
            .buyerName("Nguyen Van A")
            .buyerPhone("012456789")
            .buyerTaxCode("0123456789-001")

            // detail item breakdown
            .item(
                PaymentLinkItem.builder()
                    .name("iPhone 15 Pro Case")
                    .quantity(2)
                    .price(25000L)
                    .unit("piece")
                    .taxPercentage(TaxPercentage.TEN)
                    .build())
            .item(
                PaymentLinkItem.builder()
                    .name("Screen Protector")
                    .quantity(1)
                    .price(15000L)
                    .unit("piece")
                    .taxPercentage(TaxPercentage.TEN)
                    .build())
            .item(
                PaymentLinkItem.builder()
                    .name("Wireless Charge")
                    .quantity(1)
                    .price(50000L)
                    .unit("piece")
                    .taxPercentage(TaxPercentage.TEN)
                    .build())

            // invoice configuration
            .invoice(
                InvoiceRequest.builder()
                    .taxPercentage(TaxPercentage.TEN) // overall tax percent
                    .buyerNotGetInvoice(false) // customer wants invoice
                    .build())
            .expiredAt(System.currentTimeMillis() / 1000 + 60 * 60)
            .build();
    try {
      CreatePaymentLinkResponse response = client.paymentRequests().create(paymentData);
      System.out.println(response.toString());
    } catch (APIException e) {
      e.printStackTrace();
    }
  }
}
