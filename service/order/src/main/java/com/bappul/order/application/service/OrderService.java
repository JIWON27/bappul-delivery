package com.bappul.order.application.service;

import static com.bappul.order.exception.ServiceExceptionCode.JSON_SERIALIZATION_ERROR;

import com.bappul.order.adapter.response.CartItemCalculateResponse;
import com.bappul.order.adapter.response.OptionPrice;
import com.bappul.order.application.event.contracts.common.AggregateType;
import com.bappul.order.application.event.contracts.common.EventType;
import com.bappul.order.application.event.contracts.order.OrderAcceptEvent;
import com.bappul.order.application.event.contracts.order.OrderCancelEvent;
import com.bappul.order.application.event.contracts.order.OrderReadyEvent;
import com.bappul.order.application.event.contracts.order.OrderRejectEvent;
import com.bappul.order.application.event.producer.OutboxRecorded;
import com.bappul.order.application.mapper.OrderMapper;
import com.bappul.order.application.validator.OrderValidator;
import com.bappul.order.domain.entitiy.Order;
import com.bappul.order.domain.entitiy.OrderItem;
import com.bappul.order.domain.entitiy.OrderItemOption;
import com.bappul.order.domain.entitiy.OrderStatus;
import com.bappul.order.domain.entitiy.OutBoxEvent;
import com.bappul.order.domain.entitiy.OutboxStatus;
import com.bappul.order.domain.repository.OrderItemOptionRepository;
import com.bappul.order.domain.repository.OrderItemRepository;
import com.bappul.order.domain.repository.OrderRepository;
import com.bappul.order.domain.repository.OutboxEventRepository;
import com.bappul.order.port.PaymentCommandPort;
import com.bappul.order.web.v1.request.OrderRequest;
import com.bappul.order.web.v1.response.OrderResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ServiceException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final OrderItemOptionRepository orderItemOptionRepository;
  private final OutboxEventRepository outboxEventRepository;

  private final OrderQuoteService orderQuoteService;
  private final PaymentCommandPort paymentCommandPort;

  private final OrderValidator orderValidator;
  private final ObjectMapper objectMapper;
  private final OrderMapper  orderMapper;

  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public OrderResponse createOrder(OrderRequest request, Long userId) {
    if (orderValidator.idempotencyKeyGuard(request.getIdempotencyKey())) {
      Order order = orderValidator.getOrderByIdempotencyKey(request.getIdempotencyKey());
      return orderMapper.toOrderResponse(order);
    }

    CalculateResult calculateResult = orderQuoteService.calculatePayablePrice(request, userId);

    BigDecimal payablePrice = calculateResult.getPayableTotalPrice();
    BigDecimal discountPrice = calculateResult.getOrderDiscountPrice();
    List<CartItemCalculateResponse> cartItemCalculateResponses = calculateResult.getCartItemCalculateResponses();

    Order order = orderMapper.toOrder(
        UUID.randomUUID(),
        null,
        userId,
        request,
        calculateResult,
        OrderStatus.CREATED);
    orderRepository.save(order);

    String merchantUid = paymentCommandPort.fakePreparePayment(order.getId(), payablePrice);
    order.updateMerchantUid(merchantUid);

    List<OrderItem> orderItems = new ArrayList<>();
    List<OrderItemOption> orderItemOptions = new ArrayList<>();
    int menuCount = cartItemCalculateResponses.size();

    for (CartItemCalculateResponse cartItem : cartItemCalculateResponses) {
      BigDecimal perLineDiscount = discountPrice.divide(new BigDecimal(menuCount), RoundingMode.DOWN);
      OrderItem orderItem = orderMapper.toOrderItem(order, cartItem, perLineDiscount, BigDecimal.ZERO, 0);
      orderItems.add(orderItem);

      for (OptionPrice optionPrice : cartItem.getOptionPrices()) {
        OrderItemOption orderLineOption = orderMapper.toOrderItemOption(orderItem, optionPrice);
        orderItemOptions.add(orderLineOption);
      }
    }

    orderItemRepository.saveAll(orderItems);
    orderItemOptionRepository.saveAll(orderItemOptions);

    return orderMapper.toOrderResponse(order);
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

    UUID eventId = UUID.randomUUID();

    OrderAcceptEvent event = new OrderAcceptEvent(orderId, userId);
    String payload = toJson(event);

    outboxEventRepository.save(OutBoxEvent.builder()
        .eventId(eventId)
        .eventType(EventType.ORDER_ACCEPT)
        .aggregateId(order.getId())
        .aggregateType(AggregateType.ORDER)
        .partitionKey(order.getId().toString())
        .payload(payload)
        .status(OutboxStatus.PENDING)
        .occurredAt(LocalDateTime.now())
        .build());
    OutboxRecorded outboxRecorded = new OutboxRecorded(eventId, EventType.ORDER_ACCEPT);
    eventPublisher.publishEvent(outboxRecorded);

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

    OrderReadyEvent event = OrderReadyEvent.builder()
        .orderId(order.getId())
        .latitude(37.5000) // TODO storeId로 Store 조회 후 할당
        .longitude(127.0300) // TODO storeId로 Store 조회 후 할당
        .adminCode("11680640") // TODO storeId로 Store 조회 후 할당
        .legalCode("11680101") // TODO storeId로 Store 조회 후 할당
        .build();
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
