package vn.payos.examples;

import vn.payos.PayOS;
import vn.payos.core.ClientOptions;
import vn.payos.core.ClientOptions.LogLevel;
import vn.payos.exception.APIException;

public class TestExample {
  public static void main(String[] args) {
    PayOS client =
        new PayOS(
            ClientOptions.builder()
                .clientId("5cde3df2-9fd1-4ded-bf29-45dc455725ca")
                .apiKey("ab7a2dca-e4e6-4631-a2ba-46051fc3f24d")
                .checksumKey("a78c51160cc58d5c319930ef023d3865135a9e880a3cd2e598da583aec277852")
                .baseURL("https://dev.api-merchant.payos.vn")
                .logLevel(LogLevel.DEBUG)
                .build());
    try {

      client
          .paymentRequests()
          .invoices()
          .download("a31a7016-7fea-4705-a5f2-04af77069e86", 1757060813L);
      ;
    } catch (APIException e) {
      System.out.println(e.getErrorDesc());
      e.printStackTrace();
    }
  }
}
