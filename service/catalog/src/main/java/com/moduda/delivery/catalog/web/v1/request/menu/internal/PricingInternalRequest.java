package com.moduda.delivery.catalog.web.v1.request.menu.internal;

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
