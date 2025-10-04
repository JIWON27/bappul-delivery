package com.bappul.delivery.order.application.service;

import static com.bappul.delivery.order.exception.ServiceExceptionCode.JSON_SERIALIZATION_ERROR;

import com.bappul.delivery.order.application.event.contracts.common.AggregateType;
import com.bappul.delivery.order.application.event.contracts.common.EventType;
import com.bappul.delivery.order.application.event.contracts.order.OrderCancelEvent;
import com.bappul.delivery.order.application.event.contracts.order.OrderReadyEvent;
import com.bappul.delivery.order.application.event.contracts.order.OrderRejectEvent;
import com.bappul.delivery.order.application.event.producer.OutboxRecorded;
import com.bappul.delivery.order.application.validator.OrderValidator;
import com.bappul.delivery.order.client.CatalogClient;
import com.bappul.delivery.order.client.CouponClient;
import com.bappul.delivery.order.client.PaymentClient;
import com.bappul.delivery.order.client.request.CouponDiscountCalculateRequest;
import com.bappul.delivery.order.client.request.PaymentCreateRequest;
import com.bappul.delivery.order.client.request.PricingInternalRequest;
import com.bappul.delivery.order.client.response.CartItemCalculateResponse;
import com.bappul.delivery.order.client.response.CouponDiscountCalculateResponse;
import com.bappul.delivery.order.client.response.OptionPerPrice;
import com.bappul.delivery.order.client.response.PricingInternalResponse;
import com.bappul.delivery.order.domain.entitiy.Order;
import com.bappul.delivery.order.domain.entitiy.OrderLine;
import com.bappul.delivery.order.domain.entitiy.OrderLineOption;
import com.bappul.delivery.order.domain.entitiy.OrderStatus;
import com.bappul.delivery.order.domain.entitiy.OutBoxEvent;
import com.bappul.delivery.order.domain.entitiy.OutboxStatus;
import com.bappul.delivery.order.domain.repository.OrderLineOptionRepository;
import com.bappul.delivery.order.domain.repository.OrderLineRepository;
import com.bappul.delivery.order.domain.repository.OrderRepository;
import com.bappul.delivery.order.domain.repository.OutboxEventRepository;
import com.bappul.delivery.order.web.v1.request.OrderRequest;
import com.bappul.delivery.order.web.v1.response.OrderResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ServiceException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderLineRepository orderLineRepository;
  private final OrderLineOptionRepository orderLineOptionRepository;
  private final OutboxEventRepository outboxEventRepository;

  private final OrderValidator orderValidator;
  private final ObjectMapper objectMapper;
  private final KafkaTemplate<String, String> kafkaTemplate;

  private final CatalogClient catalogClient;
  private final CouponClient couponClient;
  private final PaymentClient paymentClient;
  private final ApplicationEventPublisher eventPublisher;

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
      CouponDiscountCalculateResponse discountResponse = couponClient.getCouponDiscountCalculate(
          couponInternalRequest);
      discountPrice = discountResponse.getDiscount();
    }

    // payablePrice 결제할 가격 계산 -> 총 가격 - 쿠폰 할인가 + 배달비 = 최종 결제 금액
    BigDecimal payablePrice = quote.getTotalPrice().add(request.getDeliveryFee())
        .subtract(discountPrice);

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
  public void cancel(Long orderId, Long userId) {
    Order order = orderValidator.getOrderById(orderId);
    order.markAsCanceled();

    UUID eventId = UUID.randomUUID();

    OrderCancelEvent event = new OrderCancelEvent(orderId, userId);
    String payload = toJson(event);

    outboxEventRepository.save(OutBoxEvent.builder()
        .eventId(eventId)
        .eventType(EventType.ORDER_CANCEL)
        .aggregateId(order.getId())
        .aggregateType(AggregateType.ORDER)
        .partitionKey(order.getId().toString())
        .payload(payload)
        .status(OutboxStatus.PENDING)
        .occurredAt(LocalDateTime.now())
        .build());
    OutboxRecorded outboxRecorded = new OutboxRecorded(eventId, EventType.ORDER_CANCEL);
    eventPublisher.publishEvent(outboxRecorded);
  }

  @Transactional
  public void accept(Long storeId, Long orderId, Long userId) {
    Order order = orderValidator.getOrderById(orderId);
    orderValidator.validateBelongsToStore(order.getStoreId(), storeId);

    if (order.getOrderStatus() == OrderStatus.ACCEPTED) {
      return;
    }

    orderValidator.validateAcceptable(order);
    order.markAsAccepted();

    // TODO 주문 수락 알림 기능
  }

  @Transactional
  public void reject(Long storeId, Long orderId, Long userId) {
    Order order = orderValidator.getOrderById(orderId);
    orderValidator.validateBelongsToStore(order.getStoreId(), storeId);

    orderValidator.validateRejectable(order);
    order.markAsRejected(); // TODO 주문 거절 이유 추가

    UUID eventId = UUID.randomUUID();

    OrderRejectEvent event = new OrderRejectEvent(orderId, userId);
    String payload = toJson(event);

    outboxEventRepository.save(OutBoxEvent.builder()
        .eventId(eventId)
        .eventType(EventType.ORDER_REJECTED)
        .aggregateId(order.getId())
        .aggregateType(AggregateType.ORDER)
        .partitionKey(order.getId().toString())
        .payload(payload)
        .status(OutboxStatus.PENDING)
        .occurredAt(LocalDateTime.now())
        .build());
    OutboxRecorded outboxRecorded = new OutboxRecorded(eventId, EventType.ORDER_REJECTED);
    eventPublisher.publishEvent(outboxRecorded);

    // TODO 주문 거절 알림 기능
  }

  @Transactional
  public void ready(Long storeId, Long orderId) {
    Order order = orderValidator.getOrderById(orderId);
    orderValidator.validateBelongsToStore(order.getStoreId(), storeId);

    orderValidator.validateReadyable(order);
    order.markAsReady();

    UUID eventId = UUID.randomUUID();

    OrderReadyEvent event = new OrderReadyEvent(orderId);
    String payload = toJson(event);

    outboxEventRepository.save(OutBoxEvent.builder()
        .eventId(eventId)
        .eventType(EventType.ORDER_READY)
        .aggregateId(order.getId())
        .aggregateType(AggregateType.ORDER)
        .partitionKey(order.getId().toString())
        .payload(payload)
        .status(OutboxStatus.PENDING)
        .occurredAt(LocalDateTime.now())
        .build());

    OutboxRecorded outboxRecorded = new OutboxRecorded(eventId, EventType.ORDER_READY);
    eventPublisher.publishEvent(outboxRecorded);

    // TODO 주문 준비 완료 알림
  }

  private String toJson(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new ServiceException(JSON_SERIALIZATION_ERROR);
    }
  }
}
