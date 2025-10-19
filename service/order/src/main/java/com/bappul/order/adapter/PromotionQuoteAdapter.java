package com.bappul.order.adapter;

import com.bappul.order.adapter.request.CouponDiscountCalculateRequest;
import com.bappul.order.adapter.response.CouponDiscountCalculateResponse;
import com.bappul.order.port.PromotionQuotePort;
import com.bappul.order.web.v1.request.OrderRequest;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionQuoteAdapter implements PromotionQuotePort {

  private final PromotionClient promotionClient;

  @Override
  public CouponDiscountCalculateResponse getDiscountPrice(OrderRequest orderRequest, Long userId, BigDecimal totalPrice) {
    CouponDiscountCalculateRequest request = CouponDiscountCalculateRequest.builder()
        .couponId(orderRequest.getCouponId())
        .storeId(orderRequest.getStoreId())
        .userId(userId)
        .price(totalPrice)
        .build();
    return promotionClient.getCouponDiscountCalculate(request);
  }
}
