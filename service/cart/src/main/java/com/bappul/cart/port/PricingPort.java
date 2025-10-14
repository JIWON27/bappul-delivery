package com.bappul.cart.port;

import com.bappul.cart.adapter.pricing.response.PricingInternalResponse;
import com.bappul.cart.web.v1.request.CartItemRequest;
import java.util.List;

public interface PricingPort {
  PricingInternalResponse getQuote(Long storeId, List<CartItemRequest> items);
}
