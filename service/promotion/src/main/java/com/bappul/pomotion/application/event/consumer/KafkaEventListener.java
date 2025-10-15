package com.bappul.pomotion.application.event.consumer;

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

  private final CouponProcessor couponProcessor;

  @KafkaListener(topics = "coupon-used", groupId = "promotion")
  public void onCouponUsedEvent(ConsumerRecord<String, String> record, Acknowledgment ack){
    handle(record, ack, () -> couponProcessor.processCouponUsed(record));
  }

  @KafkaListener(topics = "coupon-rollback", groupId = "promotion")
  public void onCouponRollbackEvent(ConsumerRecord<String, String> record, Acknowledgment ack){
    handle(record, ack, () -> couponProcessor.processCouponRollback(record));
  }

  @KafkaListener(topics = "first-come-coupon-request", groupId = "promotion")
  public void onFirstComeCouponIssueRequested(ConsumerRecord<String, String> record, Acknowledgment ack){
    handle(record, ack, () -> couponProcessor.processFirstCouponIssue(record));
  }

  private void handle(ConsumerRecord<String, String> record, Acknowledgment ack, Runnable processor) {
    try {
      log.info("[promotion] Consume topic={} partition={} offset={} key={}",
          record.topic(), record.partition(), record.offset(), record.key());
      processor.run();
      ack.acknowledge();
    } catch (Exception e) {
      log.error("[promotion] Processing failed topic={} partition={} offset={} key={}",
          record.topic(), record.partition(), record.offset(), record.key(), e);
      throw new ServiceException();
    }
  }
}
