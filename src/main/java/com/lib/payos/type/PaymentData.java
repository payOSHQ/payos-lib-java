package com.lib.payos.type;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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
