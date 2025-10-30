package com.bappul.order.application.service;

import com.bappul.event.outbox.OutboxRecorder;
import com.bappul.order.adapter.response.CartItemCalculateResponse;
import com.bappul.order.adapter.response.OptionPrice;
import com.bappul.order.application.event.contracts.common.AggregateType;
import com.bappul.order.application.event.contracts.common.EventType;
import com.bappul.order.application.event.contracts.order.OrderAcceptEvent;
import com.bappul.order.application.event.contracts.order.OrderCancelEvent;
import com.bappul.order.application.event.contracts.order.OrderReadyEvent;
import com.bappul.order.application.event.contracts.order.Reason;
import com.bappul.order.application.mapper.OrderMapper;
import com.bappul.order.application.validator.OrderValidator;
import com.bappul.order.domain.entitiy.Order;
import com.bappul.order.domain.entitiy.OrderItem;
import com.bappul.order.domain.entitiy.OrderItemOption;
import com.bappul.order.domain.entitiy.OrderStatus;
import com.bappul.order.domain.repository.OrderItemOptionRepository;
import com.bappul.order.domain.repository.OrderItemRepository;
import com.bappul.order.domain.repository.OrderRepository;
import com.bappul.order.port.PaymentCommandPort;
import com.bappul.order.web.v1.request.OrderRequest;
import com.bappul.order.web.v1.response.OrderResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final OrderItemOptionRepository orderItemOptionRepository;

  private final OutboxRecorder outboxRecorder;
  private final OrderQuoteService orderQuoteService;
  private final PaymentCommandPort paymentCommandPort;

  private final OrderValidator orderValidator;
  private final OrderMapper  orderMapper;


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
        userId,
        request,
        calculateResult,
        OrderStatus.CREATED);
    orderRepository.save(order);

    paymentCommandPort.createPaymentIntent(
        order.getId(),
        payablePrice,
        request.getPgProvider(), request.getPayMethod());

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
    orderValidator.ensureOwnedBy(order, userId);

    if (order.getOrderStatus() == OrderStatus.ACCEPTED) {
      return;
    }

    order.markAsCanceled();

    outboxRecorder.record(
        EventType.ORDER_CANCEL.name(),
        AggregateType.ORDER.name(),
        EventType.ORDER_CANCEL.getKafkaTopic(),
        order.getId(),
        order.getId().toString(),
        () -> OrderCancelEvent.builder()
            .orderId(orderId)
            .reason(Reason.USER_REQUEST)
            .totalRefundPrice(order.getPayableTotalPrice())
            .build()
    );
  }

  @Transactional
  public void accept(Long storeId, Long orderId) {
    Order order = orderValidator.getOrderById(orderId);
    orderValidator.validateBelongsToStore(order.getStoreId(), storeId);

    if (order.getOrderStatus() == OrderStatus.ACCEPTED) {
      return;
    }

    orderValidator.validateAcceptable(order);
    order.markAsAccepted();

    outboxRecorder.record(
        EventType.ORDER_ACCEPT.name(),
        AggregateType.ORDER.name(),
        EventType.ORDER_ACCEPT.getKafkaTopic(),
        order.getId(),
        order.getId().toString(),
        () -> new OrderAcceptEvent(orderId, order.getUserId())
    );
  }

  @Transactional
  public void reject(Long storeId, Long orderId) {
    Order order = orderValidator.getOrderById(orderId);
    orderValidator.validateBelongsToStore(order.getStoreId(), storeId);
    orderValidator.validateRejectable(order);

    order.markAsRejected();

    outboxRecorder.record(
        EventType.ORDER_REJECTED.name(),
        AggregateType.ORDER.name(),
        EventType.ORDER_REJECTED.getKafkaTopic(),
        order.getId(),
        order.getId().toString(),
        () -> OrderCancelEvent.builder()
            .orderId(orderId)
            .reason(Reason.OWNER_REQUEST)
            .totalRefundPrice(order.getPayableTotalPrice())
            .build()
    );
  }

  @Transactional
  public void ready(Long storeId, Long orderId) {
    Order order = orderValidator.getOrderById(orderId);
    orderValidator.validateBelongsToStore(order.getStoreId(), storeId);

    orderValidator.validateReadyable(order);
    order.markAsReady();

    outboxRecorder.record(
        EventType.ORDER_READY.name(),
        AggregateType.ORDER.name(),
        EventType.ORDER_READY.getKafkaTopic(),
        order.getId(),
        order.getId().toString(),
        () -> OrderReadyEvent.builder()
            .orderId(order.getId())
            .latitude(37.5000) // TODO storeId로 Store 조회 후 할당
            .longitude(127.0300) // TODO storeId로 Store 조회 후 할당
            .adminCode("11680640") // TODO storeId로 Store 조회 후 할당
            .legalCode("11680101") // TODO storeId로 Store 조회 후 할당
            .build()
    );
  }
}
