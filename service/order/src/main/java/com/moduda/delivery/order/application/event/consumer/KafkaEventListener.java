package com.moduda.delivery.order.application.event.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventListener {

  private final KafkaEventProcessor orderEventProcessor;

  @KafkaListener(topics = "payment-success", groupId = "order")
  public void onPaymentSuccessRequested(
      @Payload String payload,
      @Header("event-id") String eventId,
      @Header(value = "event-type", required = false) String eventType,
      Acknowledgment ack
  ) throws Exception {
    log.info("[order - KafkaEventListener] 결제 성공 이벤트 수신 = {}", eventId);
    orderEventProcessor.processPaymentSuccess(eventId, eventType, payload);
    ack.acknowledge();
  }

  @KafkaListener(topics = "payment-failed", groupId = "order")
  public void onPaymentFailRequested(
      @Payload String payload,
      @Header("event-id") String eventId,
      @Header(value = "event-type", required = false) String eventType,
      Acknowledgment ack
  ) throws Exception {
    log.error("[order - KafkaEventListener] 결제 실패 이벤트 수신 = {}", eventId);
    orderEventProcessor.processPaymentFailed(eventId, eventType, payload);
    ack.acknowledge();
  }

  @KafkaListener(topics = "payment-refunded", groupId = "order")
  public void onPaymentRefundRequested(
      @Payload String payload,
      @Header("event-id") String eventId,
      @Header(value = "event-type", required = false) String eventType,
      Acknowledgment ack
  ) throws Exception {
    log.info("[order - KafkaEventListener] 결제 환불 이벤트 수신 = {}", eventId);
    orderEventProcessor.processPaymentRefunded(eventId, eventType, payload);
    ack.acknowledge();
  }

}
