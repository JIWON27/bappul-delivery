package com.moduda.delivery.catalog.web.v1.request.menu.internal;

import java.util.List;
import lombok.Getter;

@Getter
public class CartItemRequest {
  Long menuId;
  List<Long> optionValueIds;
  int quantity;
}
