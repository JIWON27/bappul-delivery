package com.moduda.delivery.order.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moduda.delivery.order.application.event.contracts.order.OrderCancelEvent;
import com.moduda.delivery.order.application.validator.OrderValidator;
import com.moduda.delivery.order.client.CatalogClient;
import com.moduda.delivery.order.client.CouponClient;
import com.moduda.delivery.order.client.PaymentClient;
import com.moduda.delivery.order.client.request.CouponDiscountCalculateRequest;
import com.moduda.delivery.order.client.request.PaymentCreateRequest;
import com.moduda.delivery.order.client.request.PricingInternalRequest;
import com.moduda.delivery.order.client.response.CartItemCalculateResponse;
import com.moduda.delivery.order.client.response.CouponDiscountCalculateResponse;
import com.moduda.delivery.order.client.response.OptionPerPrice;
import com.moduda.delivery.order.client.response.PricingInternalResponse;
import com.moduda.delivery.order.domain.entitiy.DeliveryStatus;
import com.moduda.delivery.order.domain.entitiy.Order;
import com.moduda.delivery.order.domain.entitiy.OrderLine;
import com.moduda.delivery.order.domain.entitiy.OrderLineOption;
import com.moduda.delivery.order.domain.entitiy.OrderStatus;
import com.moduda.delivery.order.domain.entitiy.PaymentStatus;
import com.moduda.delivery.order.domain.repository.OrderLineOptionRepository;
import com.moduda.delivery.order.domain.repository.OrderLineRepository;
import com.moduda.delivery.order.domain.repository.OrderRepository;
import com.moduda.delivery.order.web.v1.request.OrderRequest;
import com.moduda.delivery.order.web.v1.response.OrderResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderLineRepository orderLineRepository;
  private final OrderLineOptionRepository orderLineOptionRepository;
  private final OrderValidator orderValidator;
  private final ObjectMapper objectMapper;
  private final KafkaTemplate<String, String> kafkaTemplate;

  private final CatalogClient catalogClient;
  private final CouponClient couponClient;
  private final PaymentClient paymentClient;

  @Transactional
  public OrderResponse createOrder(OrderRequest request, Long userId) {
    orderValidator.validateIdempotencyKey(request.getIdempotencyKey());

    // 카탈로그 서비스로 부터 가격 조회
    PricingInternalRequest catalogInternalRequest = PricingInternalRequest.builder()
        .storeId(request.getStoreId())
        .items(request.getOrderItems())
        .build();
    PricingInternalResponse quote = catalogClient.calculate(catalogInternalRequest);

    BigDecimal discountPrice = BigDecimal.ZERO;
    // 조회한 가격으로 쿠폰 서비스로 보내 최종 할인가 계산
    if (Objects.nonNull(request.getCouponId())) {
      CouponDiscountCalculateRequest couponInternalRequest = CouponDiscountCalculateRequest.builder()
          .couponId(request.getCouponId())
          .storeId(request.getStoreId())
          .userId(userId)
          .price(quote.getTotalPrice())
          .build();
      CouponDiscountCalculateResponse discountResponse = couponClient.getCouponDiscountCalculate(couponInternalRequest);
      discountPrice = discountResponse.getDiscount();
    }

    // payablePrice 결제할 가격 계산 -> 총 가격 - 쿠폰 할인가 + 배달비 = 최종 결제 금액
    BigDecimal payablePrice = quote.getTotalPrice().add(request.getDeliveryFee()).subtract(discountPrice);

    Order order = Order.builder()
        .orderNo(UUID.randomUUID())
        .userId(userId)
        .idempotencyKey(request.getIdempotencyKey())
        .deliveryFee(request.getDeliveryFee())
        .couponId(request.getCouponId())
        .addressId(request.getAddressId())
        .storeId(request.getStoreId())
        .cancelReason(null)
        .payableTotal(payablePrice)
        .orderStatus(OrderStatus.CREATED)
        .deliveryStatus(DeliveryStatus.NONE)
        .paymentStatus(PaymentStatus.NONE)
        .build();

    orderRepository.save(order);

    List<OrderLine> orderLines = new ArrayList<>();
    List<OrderLineOption> orderLineOptions = new ArrayList<>();

    for (CartItemCalculateResponse cartItem : quote.getItems()) {
      int menuCount = quote.getItems().size();

      OrderLine orderLine = OrderLine.builder()
          .order(order)
          .menuId(cartItem.getMenuId())
          .menuName(cartItem.getMenuName())
          .quantity(cartItem.getQuantity())
          .basePrice(cartItem.getBasePrice())
          .unitPrice(cartItem.getUnitPrice())
          .lineDiscount(discountPrice.divide(new BigDecimal(menuCount), RoundingMode.DOWN))
          .lineTotal(cartItem.getLineTotal())
          .refundedPrice(BigDecimal.ZERO)
          .refundedQuantity(0)
          .build();

      orderLines.add(orderLine);

      for (OptionPerPrice optionPerPrice : cartItem.getOptionPerPrices()) {
        OrderLineOption orderLineOption = OrderLineOption.builder()
            .orderLine(orderLine)
            .optionValueId(optionPerPrice.getOptionValueId())
            .optionName(optionPerPrice.getOptionName())
            .optionPrice(optionPerPrice.getOptionPrice())
            .build();
        orderLineOptions.add(orderLineOption);
      }
    }

    orderLineRepository.saveAll(orderLines);
    orderLineOptionRepository.saveAll(orderLineOptions);

    // Payment 서비스로 결제 준비 요청
    PaymentCreateRequest paymentCreateRequest = PaymentCreateRequest.builder()
        .orderId(order.getId())
        .payablePrice(payablePrice)
        .build();
    String merchantUid = paymentClient.fakePreparePayment(paymentCreateRequest);

    return OrderResponse.builder()
        .orderId(order.getId())
        .merchantUid(merchantUid)
        .orderTotal(payablePrice)
        .build();
  }

  @Transactional
  public void cancel(Long orderId, Long userId){
    try {
      // TODO 주문 취소 가능성 검증 로직 추가 ex. 메뉴 조리중 등
      Order order = orderValidator.getOrderById(orderId);
      order.markAsCanceled();

      OrderCancelEvent evt = new OrderCancelEvent(orderId, userId);
      UUID eventId = UUID.randomUUID();

      Message<String> msg = MessageBuilder
          .withPayload(objectMapper.writeValueAsString(evt))
          .setHeader(KafkaHeaders.TOPIC, "order-cancel")
          .setHeader(KafkaHeaders.KEY, String.valueOf(orderId))
          .setHeader("event-id", eventId.toString())
          .setHeader("event-type", "OrderCancelRequested")
          .setHeader("occurred-at", Instant.now().toString())
          .build();
      kafkaTemplate.send(msg);
    }catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
