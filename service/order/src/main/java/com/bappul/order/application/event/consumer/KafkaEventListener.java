package com.bappul.order.application.event.consumer;

import exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventListener {

  private final KafkaEventProcessor orderEventProcessor;

  @KafkaListener(topics = "payment-success", groupId = "order")
  public void onPaymentSuccessEvent(ConsumerRecord<String, String> record, Acknowledgment ack) throws Exception {
    handle(record, ack, () -> orderEventProcessor.processPaymentSuccess(record));
  }

  @KafkaListener(topics = "payment-failed", groupId = "order")
  public void onPaymentFailEvent(ConsumerRecord<String, String> record, Acknowledgment ack) throws Exception {
    handle(record, ack, () -> orderEventProcessor.processPaymentFail(record));
  }

  @KafkaListener(topics = "payment-refunded", groupId = "order")
  public void onPaymentRefundEvent(ConsumerRecord<String, String> record, Acknowledgment ack) throws Exception {
    handle(record, ack, () -> orderEventProcessor.processPaymentRefund(record));
  }

  @KafkaListener(topics = "delivery-complete", groupId = "order")
  public void onDeliveryCompleteEvent(ConsumerRecord<String, String> record, Acknowledgment ack) throws Exception {
    handle(record, ack, () -> orderEventProcessor.processDeliveryComplete(record));
  }

  @KafkaListener(topics = "delivery-pickup", groupId = "order")
  public void onDeliveryPickUpEvent(ConsumerRecord<String, String> record, Acknowledgment ack) throws Exception {
    handle(record, ack, () -> orderEventProcessor.processDeliveryPickUp(record));
  }

  private void handle(ConsumerRecord<String, String> record, Acknowledgment ack, Runnable processor) {
    try {
      log.info("[주문 서비스] Consume topic={} partition={} offset={} key={}",
          record.topic(), record.partition(), record.offset(), record.key());
      processor.run();
      ack.acknowledge();
    } catch (Exception e) {
      log.error("[주문 서비스] Processing failed topic={} partition={} offset={} key={}",
          record.topic(), record.partition(), record.offset(), record.key(), e);
      throw new ServiceException();
    }
  }

}
