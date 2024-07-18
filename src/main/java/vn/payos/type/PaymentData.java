package vn.payos.type;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@Jacksonized
public class PaymentData {
  @NonNull
  private Long orderCode;
  @NonNull
  private Integer amount;
  @NonNull
  private String description;
  private List<ItemData> items;
  @NonNull
  private String cancelUrl;
  @NonNull
  private String returnUrl;
  private String signature;
  private String buyerName;
  private String buyerEmail;
  private String buyerPhone;
  private String buyerAddress;
  private Integer expiredAt;

  public void addItem(ItemData item) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(item);
  }

  public static class PaymentDataBuilder {
    private List<ItemData> items = new ArrayList<>();

    public PaymentDataBuilder item(ItemData item) {
      this.items.add(item);
      return this;
    }
  }
}