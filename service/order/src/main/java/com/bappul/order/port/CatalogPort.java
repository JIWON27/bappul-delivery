package com.bappul.order.port;

import com.bappul.order.adapter.response.PricingInternalResponse;
import com.bappul.order.adapter.response.StoreLocationResponse;
import com.bappul.order.web.v1.request.OrderItemRequest;
import java.util.List;

public interface CatalogPort {
  PricingInternalResponse getQuote(Long storeId, List<OrderItemRequest> items);
  StoreLocationResponse getLocation(Long storeId);
}
