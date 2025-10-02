package com.moduda.delivery.order.client.request;

import com.moduda.delivery.order.web.v1.request.OrderItemRequest;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PricingInternalRequest {
  Long storeId;
  List<OrderItemRequest> items;
}
