package com.bappul.delivery.application.event.consumer;

import com.bappul.event.kafka.KafkaEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryEventListener {

  private final DeliveryEventProcessor eventProcessor;
  private final KafkaEventListener kafkaEventListener;

  @KafkaListener(topics = "order-accept", groupId = "delivery")
  public void onOrderAcceptEvent(ConsumerRecord<String ,String> record, Acknowledgment ack) {
    kafkaEventListener.handle(record, ack, () -> eventProcessor.processOrderAcceptEvent(record));
  }

  @KafkaListener(topics = "order-ready", groupId = "delivery")
  public void onOrderReadyEvent(ConsumerRecord<String ,String> record, Acknowledgment ack) {
    kafkaEventListener.handle(record, ack, () -> eventProcessor.processOrderReadyEvent(record));
  }
}
