package com.bappul.order.port;

import com.bappul.order.adapter.response.CouponDiscountCalculateResponse;
import com.bappul.order.web.v1.request.OrderRequest;
import java.math.BigDecimal;

public interface PromotionQuotePort {
  CouponDiscountCalculateResponse getDiscountPrice(OrderRequest request, Long userId, BigDecimal totalPrice);
}
