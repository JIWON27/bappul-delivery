package com.bappul.cart.application.event.consumer;

import com.bappul.event.kafka.KafkaEventListener;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartEventListener {

  private final KafkaEventListener kafkaEventListener;
  private final CartEventProcessor cartEventProcessor;

  @KafkaListener(topics = "cart-clear", groupId = "cart")
  public void onCartClearEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
    kafkaEventListener.handle(record, ack, () -> cartEventProcessor.processCartClearEvent(record));
  }
}
