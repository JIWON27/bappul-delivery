package com.bappul.order.client.request;

import java.util.List;
import lombok.Getter;

@Getter
public class CartItemRequest {
  Long menuId;
  List<Long> optionValueIds;
  int quantity;
}
