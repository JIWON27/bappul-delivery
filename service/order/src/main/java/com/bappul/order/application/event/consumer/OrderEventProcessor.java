package com.bappul.order.application.event.consumer;

import com.bappul.event.inbox.InboxEventRepository;
import com.bappul.event.kafka.InboxManager;
import com.bappul.event.outbox.OutBoxEvent;
import com.bappul.event.outbox.OutboxEventRepository;
import com.bappul.event.outbox.OutboxRecorded;
import com.bappul.event.outbox.OutboxStatus;
import com.bappul.order.application.event.contracts.common.AggregateType;
import com.bappul.order.application.event.contracts.common.EventType;
import com.bappul.order.application.event.contracts.coupon.CouponEvent;
import com.bappul.order.application.event.contracts.delivery.DeliveryCompleteEvent;
import com.bappul.order.application.event.contracts.delivery.DeliveryPickUpEvent;
import com.bappul.order.application.event.contracts.payment.PaymentFailEvent;
import com.bappul.order.application.event.contracts.payment.PaymentRefundedEvent;
import com.bappul.order.application.event.contracts.payment.PaymentSuccessEvent;
import com.bappul.order.application.validator.OrderValidator;
import com.bappul.order.domain.entitiy.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventProcessor {

  private final OutboxEventRepository outboxEventRepository;
  private final InboxEventRepository inboxEventRepository;
  private final InboxManager inboxManager;
  private final OrderValidator orderValidator;
  private final ObjectMapper objectMapper;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void processPaymentSuccess(ConsumerRecord<String, String> record) {
    handleEvent(record, PaymentSuccessEvent.class, this::processPaymentSuccessLogic);
  }

  @Transactional
  public void processPaymentFail(ConsumerRecord<String, String> record) {
    handleEvent(record, PaymentFailEvent.class, this::processPaymentFailedLogic);
  }

  @Transactional
  public void processPaymentRefund(ConsumerRecord<String, String> record) {
    handleEvent(record, PaymentRefundedEvent.class, this::processPaymentRefundLogic);
  }

  @Transactional
  public void processDeliveryPickUp(ConsumerRecord<String, String> record) {
    handleEvent(record, DeliveryPickUpEvent.class, this::processDeliveryPickUpLogic);
  }

  @Transactional
  public void processDeliveryComplete(ConsumerRecord<String, String> record) {
    handleEvent(record, DeliveryCompleteEvent.class, this::processDeliveryCompleteLogic);
  }

  private void processPaymentSuccessLogic(PaymentSuccessEvent event) throws JsonProcessingException {
    Order order = orderValidator.getOrderById(event.getOrderId());
    OutboxRecorded outboxRecorded = recordPaymentOutboxEvent(order, EventType.COUPON_USED);
    order.markAsPaid();
    eventPublisher.publishEvent(outboxRecorded);
  }

  private void processPaymentFailedLogic(PaymentFailEvent event) {
    Order order = orderValidator.getOrderById(event.getOrderId());
    order.markAsCanceled();
  }

  private void processPaymentRefundLogic(PaymentRefundedEvent event) throws JsonProcessingException {
    Order order = orderValidator.getOrderById(event.getOrderId());
    OutboxRecorded outboxRecorded = recordPaymentOutboxEvent(order, EventType.COUPON_ROLLBACK);
    order.markAsRefunded();
    eventPublisher.publishEvent(outboxRecorded);
  }

  private void processDeliveryPickUpLogic(DeliveryPickUpEvent event) {
    Order order = orderValidator.getOrderById(event.getOrderId());
    order.markAsPickUp();
  }

  private void processDeliveryCompleteLogic(DeliveryCompleteEvent event) {
    Order order = orderValidator.getOrderById(event.getOrderId());
    order.markAsCompleted();
  }

  private OutboxRecorded recordPaymentOutboxEvent(Order order, EventType eventType) throws JsonProcessingException {
    UUID eventId = UUID.randomUUID();

    CouponEvent event = CouponEvent.builder()
        .eventId(eventId.toString())
        .eventType(eventType.name())
        .orderId(order.getId())
        .couponId(order.getCouponId())
        .userId(order.getUserId())
        .build();

    String payload = convert(event);
    outboxEventRepository.save(OutBoxEvent.builder()
        .eventId(eventId)
        .eventType(eventType)
        .aggregateId(order.getId())
        .aggregateType(AggregateType.ORDER)
        .partitionKey(order.getId().toString())
        .payload(payload)
        .status(OutboxStatus.PENDING)
        .occurredAt(LocalDateTime.now())
        .build());
    return new OutboxRecorded(eventId, eventType);
  }
}
