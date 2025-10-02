package com.moduda.delivery.cart.client.request;

import com.moduda.delivery.cart.web.v1.request.CartItemRequest;
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
  List<CartItemRequest> items;
}
