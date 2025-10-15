package com.bappul.pomotion.application.service;

import com.bappul.pomotion.application.event.contracts.common.EventType;
import com.bappul.pomotion.application.event.contracts.coupon.FirstComeCouponIssueEvent;
import com.bappul.pomotion.application.validator.CouponValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirstComeCouponIssueService {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final CouponValidator couponValidator;
  private final ObjectMapper objectMapper;

  @Transactional
  public void firstComeCouponIssue(Long couponPolicyId, Long userId) {
    couponValidator.validateAlreadyIssued(couponPolicyId, userId);

    try {
      UUID eventId = UUID.randomUUID();

      FirstComeCouponIssueEvent evt = FirstComeCouponIssueEvent.builder()
          .eventId(eventId.toString())
          .eventType(EventType.FIRST_COME_COUPON_ISSUE.name())
          .userId(userId)
          .couponPolicyId(couponPolicyId)
          .build();

      Message<String> msg = MessageBuilder
          .withPayload(objectMapper.writeValueAsString(evt))
          .setHeader(KafkaHeaders.TOPIC, "first-come-coupon-request")
          .setHeader(KafkaHeaders.KEY, String.valueOf(couponPolicyId))
          .setHeader("event-id", eventId.toString())
          .setHeader("event-type", EventType.FIRST_COME_COUPON_ISSUE.name())
          .setHeader("occurred-at", Instant.now().toString())
          .build();
      kafkaTemplate.send(msg);
    }catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
