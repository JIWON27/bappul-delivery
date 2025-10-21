package com.bappul.delivery.application.event.consumer;

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

  private final KafkaEventProcessor kafkaEventProcessor;

  @KafkaListener(topics = "order-accept", groupId = "delivery")
  public void onOrderAcceptEvent(
      @Payload String payload,
      @Header("event-id") String eventId,
      @Header(value = "event-type", required = false) String eventType,
      Acknowledgment ack
  ) throws Exception {
    log.info("[delivery - KafkaEventListener] 주문 승인 이벤트 수신 = {}", eventId);
    kafkaEventProcessor.processOrderAcceptEvent(eventId, eventType, payload);
    ack.acknowledge();
  }

  @KafkaListener(topics = "order-ready", groupId = "delivery")
  public void onOrderReadyEvent(
      @Payload String payload,
      @Header("event-id") String eventId,
      @Header(value = "event-type", required = false) String eventType,
      Acknowledgment ack
  ) throws Exception {
    log.info("[delivery - KafkaEventListener] 주문 준비 완료 이벤트 수신 = {}", eventId);
    kafkaEventProcessor.processOrderReadyEvent(eventId, eventType, payload);
    ack.acknowledge();
  }
}
