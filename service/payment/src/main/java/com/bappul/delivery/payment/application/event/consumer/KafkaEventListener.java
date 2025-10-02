package com.bappul.delivery.payment.application.event.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventListener {

  private final KafkaEventProcessor kafkaEventProcessor;

  @KafkaListener(topics = "order-cancel", groupId = "payment")
  public void onPaymentSuccessRequested(
      @Payload String payload,
      @Header("event-id") String eventId,
      @Header(value = "event-type", required = false) String eventType,
      Acknowledgment ack
  ) throws Exception {
    kafkaEventProcessor.processOrderCancel(payload);
    ack.acknowledge();
  }
}
