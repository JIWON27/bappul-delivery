package com.moduda.delivery.order.web.v1.request;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemRequest {
  Long menuId;
  List<Long> optionValueIds;
  int quantity;
}
