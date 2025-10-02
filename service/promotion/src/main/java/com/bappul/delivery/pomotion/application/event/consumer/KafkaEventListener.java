package com.bappul.delivery.pomotion.application.event.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventListener {

  private final CouponProcessor couponProcessor;

  @KafkaListener(topics = "coupon-used", groupId = "promotion")
  public void onCouponUsedEvent(@Payload String payload, Acknowledgment ack) throws Exception {
    log.info("[promotion - KafkaEventListener] 쿠폰 사용 이벤트 수신");
    couponProcessor.processCouponUsed(payload);
    ack.acknowledge();
  }

  @KafkaListener(topics = "coupon-rollback", groupId = "promotion")
  public void onCouponRollbackEvent(@Payload String payload, Acknowledgment ack) throws Exception {
    log.info("[promotion - KafkaEventListener] 쿠폰 사용 롤백 수신");
    couponProcessor.processCouponRollback(payload);
    ack.acknowledge();
  }

  @KafkaListener(topics = "first-come-coupon-request", groupId = "promotion")
  public void onFirstComeCouponIssueRequested(@Payload String payload, Acknowledgment ack
  ) throws Exception {
    couponProcessor.processFirstCouponIssue(payload);
    ack.acknowledge();
  }
}
