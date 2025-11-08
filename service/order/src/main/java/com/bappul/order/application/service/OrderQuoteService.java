package com.bappul.order.application.service;

import com.bappul.order.adapter.response.CouponDiscountCalculateResponse;
import com.bappul.order.adapter.response.PricingInternalResponse;
import com.bappul.order.port.CatalogPort;
import com.bappul.order.port.PromotionQuotePort;
import com.bappul.order.web.v1.request.OrderItemRequest;
import com.bappul.order.web.v1.request.OrderRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderQuoteService {

  private final CatalogPort catalogPort;
  private final PromotionQuotePort promotionQuotePort;

  public PricingInternalResponse getCatalogQuote(Long storeId, List<OrderItemRequest> orderItems) {
    return catalogPort.getQuote(storeId, orderItems);
  }

  public CouponDiscountCalculateResponse getPromotionQuote(OrderRequest request, Long userId, BigDecimal totalPrice) {
    return promotionQuotePort.getDiscountPrice(request, userId, totalPrice);
  }

  public CalculateResult calculatePayablePrice(OrderRequest request, Long userId) {
    PricingInternalResponse quote = getCatalogQuote(request.getStoreId(), request.getOrderItems());

    BigDecimal orderSubtotalPrice = quote.getTotalPrice();
    BigDecimal deliveryFeePrice = quote.getDeliveryFeePrice();
    BigDecimal discountPrice = BigDecimal.ZERO;

    // 조회한 가격으로 쿠폰 서비스로 보내 최종 할인가 계산
    if (Objects.nonNull(request.getCouponId())) {
      CouponDiscountCalculateResponse discountResponse = getPromotionQuote(request,userId, orderSubtotalPrice);
      discountPrice = discountResponse.getDiscount();
    }

    BigDecimal maxDiscount = orderSubtotalPrice.add(deliveryFeePrice);
    if (discountPrice.compareTo(maxDiscount) > 0) {
      discountPrice = maxDiscount;
    }

    // payablePrice 결제할 가격 계산 -> 총 가격 - 쿠폰 할인가 + 배달비 = 최종 결제 금액
    BigDecimal payableTotalPrice = orderSubtotalPrice.add(deliveryFeePrice).subtract(discountPrice);
    if (payableTotalPrice.signum() < 0) {
      payableTotalPrice = BigDecimal.ZERO;
    }

    return CalculateResult.builder()
        .orderSubtotalPrice(orderSubtotalPrice)
        .deliveryFeePrice(deliveryFeePrice)
        .payableTotalPrice(payableTotalPrice)
        .orderDiscountPrice(discountPrice)
        .cartItemCalculateResponses(quote.getItems())
        .build();
  }

}
