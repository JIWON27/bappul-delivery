package com.bappul.delivery.payment.application.event.consumer;

import com.bappul.event.kafka.KafkaEventListener;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventListener {

  private final PaymentEventProcessor eventProcessor;
  private final KafkaEventListener kafkaEventListener;

  @KafkaListener(topics = {"order-cancel"}, groupId = "payment")
  public void onOrderCancelEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
    kafkaEventListener.handle(record, ack, () -> eventProcessor.processOrderCancel(record));
  }

  @KafkaListener(topics = {"order-reject"}, groupId = "payment")
  public void onOrderRejectEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
    kafkaEventListener.handle(record, ack, () -> eventProcessor.processOrderReject(record));
  }
}
