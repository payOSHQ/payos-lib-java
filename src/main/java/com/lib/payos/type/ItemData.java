package com.lib.payos.type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ItemData {
  private String name;
  private int quantity;
  private int price;
}
