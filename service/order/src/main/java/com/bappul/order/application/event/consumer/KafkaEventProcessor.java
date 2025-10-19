package com.bappul.order.application.event.consumer;

import static com.bappul.order.exception.ServiceExceptionCode.EVENT_ID_MISSING;
import static com.bappul.order.exception.ServiceExceptionCode.EVENT_PAYLOAD_MISSING;
import static com.bappul.order.exception.ServiceExceptionCode.EVENT_PROCESSING_FAILED;
import static com.bappul.order.exception.ServiceExceptionCode.EVENT_TYPE_MISSING;
import static com.bappul.order.exception.ServiceExceptionCode.JSON_DESERIALIZATION_ERROR;

import com.bappul.order.application.event.contracts.common.AggregateType;
import com.bappul.order.application.event.contracts.common.EventType;
import com.bappul.order.application.event.contracts.coupon.CouponEvent;
import com.bappul.order.application.event.contracts.delivery.DeliveryCompleteEvent;
import com.bappul.order.application.event.contracts.delivery.DeliveryPickUpEvent;
import com.bappul.order.application.event.contracts.payment.PaymentFailEvent;
import com.bappul.order.application.event.contracts.payment.PaymentRefundedEvent;
import com.bappul.order.application.event.contracts.payment.PaymentSuccessEvent;
import com.bappul.order.application.event.producer.OutboxRecorded;
import com.bappul.order.application.validator.OrderValidator;
import com.bappul.order.domain.entitiy.InboxStatus;
import com.bappul.order.domain.entitiy.Order;
import com.bappul.order.domain.entitiy.OutBoxEvent;
import com.bappul.order.domain.entitiy.OutboxStatus;
import com.bappul.order.domain.repository.InboxEventRepository;
import com.bappul.order.domain.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ServiceException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
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

  private <T > void handleEvent(ConsumerRecord < String, String > record, Class <T> clazz, ThrowingExceptionConsumer <T> consumer){
    String eventId = parseHeader(record, "event-id");
    String eventType = parseHeader(record, "event-type");
    String payload = record.value();

    checkRequiredEventFields(eventId, eventType, payload);
    if (!inboxManager.idempotencyGuard(eventId, eventType, payload)) {
      log.info("[inbox] duplicate eventId={}, skip", eventId);
      return;
    }

    try {
      T event = parse(payload, clazz);
      consumer.accept(event);
      inboxManager.inboxWriter(eventId, InboxStatus.PROCESSED);
    } catch (JsonProcessingException e) {
      inboxManager.inboxWriter(eventId, InboxStatus.FAILED);
      throw new ServiceException(JSON_DESERIALIZATION_ERROR);
    } catch (Exception e) {
      inboxManager.inboxWriter(eventId, InboxStatus.FAILED);
      throw new ServiceException(EVENT_PROCESSING_FAILED);
    }
  }

  private <T> T parse(String payload, Class <T> clazz) throws JsonProcessingException {
    return objectMapper.readValue(payload, clazz);
  }

  private String convert(Object obj) throws JsonProcessingException {
    return objectMapper.writeValueAsString(obj);
  }

  private String parseHeader (ConsumerRecord < String, String > record, String headerKey){
    Header header = record.headers().lastHeader(headerKey);
    return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
  }

  private void checkRequiredEventFields(String eventId, String eventType, String payload) {
    if (eventId == null || eventId.isBlank()) {
      throw new ServiceException(EVENT_ID_MISSING);
    }
    if (eventType == null || eventType.isBlank()) {
      throw new ServiceException(EVENT_TYPE_MISSING);
    }
    if (payload == null || payload.isBlank()) {
      throw new ServiceException(EVENT_PAYLOAD_MISSING);
    }
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
