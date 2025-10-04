package com.bappul.delivery.payment.application.event.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventListener {

  private final KafkaEventProcessor kafkaEventProcessor;

  @KafkaListener(topics = {"order-cancel", "order-reject"}, groupId = "payment")
  public void onOrderCancelOrReject(
      @Payload String payload,
      @Header("event-id") String eventId,
      @Header(value = "event-type", required = false) String eventType,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      Acknowledgment ack
  ) throws Exception {
    kafkaEventProcessor.processOrderCancelOrReject(payload);
    ack.acknowledge();
  }
}
