package com.bappul.order.application.mapper;

import com.bappul.order.adapter.response.CartItemCalculateResponse;
import com.bappul.order.adapter.response.OptionPrice;
import com.bappul.order.application.service.CalculateResult;
import com.bappul.order.domain.entitiy.Order;
import com.bappul.order.domain.entitiy.OrderItem;
import com.bappul.order.domain.entitiy.OrderItemOption;
import com.bappul.order.domain.entitiy.OrderStatus;
import com.bappul.order.web.v1.request.OrderRequest;
import com.bappul.order.web.v1.response.OrderResponse;
import java.math.BigDecimal;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  @Mapping(target = "orderSubtotalPrice", source = "calculateResult.orderSubtotalPrice")
  @Mapping(target = "deliveryFeePrice", source = "calculateResult.deliveryFeePrice")
  @Mapping(target = "orderDiscountPrice", source = "calculateResult.orderDiscountPrice")
  @Mapping(target = "payableTotalPrice", source = "calculateResult.payableTotalPrice")
  Order toOrder(
      UUID orderNo,
      String merchantUid,
      Long userId,
      OrderRequest request,
      CalculateResult calculateResult,
      OrderStatus orderStatus);

  @Mapping(target = "lineTotalPrice", source = "cartItem.lineTotalPrice")
  OrderItem toOrderItem(Order order, CartItemCalculateResponse cartItem, BigDecimal lineDiscount, BigDecimal refundPrice, int refundedQuantity);
  OrderItemOption toOrderItemOption(OrderItem orderItem, OptionPrice optionPrice);

  @Mapping(target = "orderId", source = "order.id")
  @Mapping(target = "orderTotalPrice", source = "order.payableTotalPrice")
  OrderResponse toOrderResponse(Order order);
}
