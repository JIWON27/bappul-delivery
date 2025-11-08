package com.bappul.order.port;

import com.bappul.order.adapter.response.PricingInternalResponse;
import com.bappul.order.web.v1.request.OrderItemRequest;
import java.util.List;

public interface CatalogQuotePort {
  PricingInternalResponse getQuote(Long storeId, List<OrderItemRequest> items);
}
