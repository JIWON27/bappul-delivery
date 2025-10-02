package com.bappul.delivery.order.application.event.consumer;

import static com.bappul.delivery.order.exception.ServiceExceptionCode.JSON_DESERIALIZATION_ERROR;
import static com.bappul.delivery.order.exception.ServiceExceptionCode.JSON_SERIALIZATION_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bappul.delivery.order.application.event.contracts.common.AggregateType;
import com.bappul.delivery.order.application.event.contracts.common.EventType;
import com.bappul.delivery.order.application.event.contracts.coupon.CouponEventPayload;
import com.bappul.delivery.order.application.event.contracts.payment.PaymentRefundedEvent;
import com.bappul.delivery.order.application.event.contracts.payment.PaymentSuccessEvent;
import com.bappul.delivery.order.application.event.producer.OutboxRecorded;
import com.bappul.delivery.order.application.validator.OrderValidator;
import com.bappul.delivery.order.domain.entitiy.InboxEvent;
import com.bappul.delivery.order.domain.entitiy.InboxStatus;
import com.bappul.delivery.order.domain.entitiy.Order;
import com.bappul.delivery.order.domain.entitiy.OutBoxEvent;
import com.bappul.delivery.order.domain.entitiy.OutboxStatus;
import com.bappul.delivery.order.domain.repository.InboxEventRepository;
import com.bappul.delivery.order.domain.repository.OutboxEventRepository;
import exception.ServiceException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventProcessor {

  private final OutboxEventRepository outboxEventRepository;
  private final InboxEventRepository inboxEventRepository;
  private final OrderValidator orderValidator;
  private final ObjectMapper objectMapper;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public void processPaymentSuccess(String eventId, String eventType, String payload) {
    InboxEvent inboxEvent = validateAndSaveInboxEvent(eventId,  eventType, payload);

    try {
      PaymentSuccessEvent paymentSuccessEvent = objectMapper.readValue(payload, PaymentSuccessEvent.class);
      Order order = orderValidator.getOrderById(paymentSuccessEvent.getOrderId());
      OutboxRecorded outboxRecorded = recordPaymentEvent(order, EventType.COUPON_USED);

      inboxEvent.markAsProcessed();

      eventPublisher.publishEvent(outboxRecorded);

    } catch (JsonProcessingException e) {
      inboxEvent.markAsFailed();
      throw new ServiceException(JSON_DESERIALIZATION_ERROR);
    }
  }

  @Transactional
  public void processPaymentFailed(String eventId, String eventType, String payload) {
    InboxEvent inboxEvent = validateAndSaveInboxEvent(eventId,  eventType, payload);

    try {
      PaymentSuccessEvent paymentSuccessEvent = objectMapper.readValue(payload, PaymentSuccessEvent.class);
      Order order = orderValidator.getOrderById(paymentSuccessEvent.getOrderId());
      order.markAsCanceled();
      inboxEvent.markAsProcessed();

      // TODO 결제 실패 알림 Notification 기능 구현

    } catch (JsonProcessingException e) {
      inboxEvent.markAsFailed();
      throw new ServiceException(JSON_DESERIALIZATION_ERROR);
    }

  }

  @Transactional
  public void processPaymentRefunded(String eventId, String eventType, String payload) {
    InboxEvent inboxEvent = validateAndSaveInboxEvent(eventId,  eventType, payload);

    try {
      PaymentRefundedEvent paymentFailEvent = objectMapper.readValue(payload, PaymentRefundedEvent.class);
      Order order = orderValidator.getOrderById(paymentFailEvent.getOrderId());
      OutboxRecorded outboxRecorded = recordPaymentEvent(order, EventType.COUPON_ROLLBACK);

      inboxEvent.markAsProcessed();

      eventPublisher.publishEvent(outboxRecorded);
    } catch (JsonProcessingException e) {
      inboxEvent.markAsFailed();
      throw new ServiceException(JSON_DESERIALIZATION_ERROR);
    }
  }

  // TODO 메서드명 조금 더 고민
  private OutboxRecorded recordPaymentEvent(Order order, EventType eventType) {
    CouponEventPayload event = CouponEventPayload.builder()
        .orderId(order.getId())
        .couponId(order.getCouponId())
        .userId(order.getUserId())
        .build();

    String payload = toJson(event);

    UUID eventId = UUID.randomUUID();
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

  private InboxEvent validateAndSaveInboxEvent(String eventId, String eventType, String payload) {
    boolean exists = inboxEventRepository.existsByEventId(eventId);
    if (exists) {
      return inboxEventRepository.findByEventId(eventId);
    }

    InboxEvent inboxEvent = InboxEvent.builder()
        .eventId(eventId)
        .eventType(eventType)
        .payload(payload)
        .status(InboxStatus.RECEIVED)
        .build();
    return inboxEventRepository.save(inboxEvent);
  }

  private String toJson(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new ServiceException(JSON_SERIALIZATION_ERROR);
    }
  }
}
