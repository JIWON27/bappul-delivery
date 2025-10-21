package com.bappul.pomotion.application.event.consumer;

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
public class CouponEventListener {

  private final CouponProcessor couponProcessor;
  private final KafkaEventListener kafkaEventListener;

  @KafkaListener(topics = "coupon-used", groupId = "promotion")
  public void onCouponUsedEvent(ConsumerRecord<String, String> record, Acknowledgment ack){
    kafkaEventListener.handle(record, ack, () -> couponProcessor.processCouponUsed(record));
  }

  @KafkaListener(topics = "coupon-rollback", groupId = "promotion")
  public void onCouponRollbackEvent(ConsumerRecord<String, String> record, Acknowledgment ack){
    kafkaEventListener.handle(record, ack, () -> couponProcessor.processCouponRollback(record));
  }

  @KafkaListener(topics = "first-come-coupon-request", groupId = "promotion")
  public void onFirstComeCouponIssueRequested(ConsumerRecord<String, String> record, Acknowledgment ack){
    kafkaEventListener.handle(record, ack, () -> couponProcessor.processFirstCouponIssue(record));
  }
}
