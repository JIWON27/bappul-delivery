package com.bappul.delivery.order.web.v1.request;

import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
  Long storeId;
  String idempotencyKey;
  Long addressId;
  Long couponId;
  Long clientPayableWon;
  List<OrderItemRequest> orderItems;
  List<Long> cartItemIds;
  BigDecimal deliveryFee;
}
