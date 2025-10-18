package com.bappul.order.port;

import com.bappul.order.adapter.request.CouponDiscountCalculateRequest;
import com.bappul.order.adapter.response.CouponDiscountCalculateResponse;

public interface PromotionQuotePort {
  CouponDiscountCalculateResponse getDiscountPrice(CouponDiscountCalculateRequest request);
}
