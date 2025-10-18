package com.bappul.order.adapter;

import com.bappul.order.adapter.request.CouponDiscountCalculateRequest;
import com.bappul.order.adapter.response.CouponDiscountCalculateResponse;
import com.bappul.order.port.PromotionQuotePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionQuoteAdapter implements PromotionQuotePort {

  private final PromotionClient promotionClient;

  @Override
  public CouponDiscountCalculateResponse getDiscountPrice(CouponDiscountCalculateRequest request) {
    return promotionClient.getCouponDiscountCalculate(request);
  }
}
