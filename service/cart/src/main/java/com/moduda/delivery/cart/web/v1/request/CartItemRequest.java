package com.moduda.delivery.cart.web.v1.request;

import java.util.List;
import lombok.Getter;

@Getter
public class CartItemRequest {
  Long menuId;
  List<Long> optionValueIds;
  int quantity;
}
