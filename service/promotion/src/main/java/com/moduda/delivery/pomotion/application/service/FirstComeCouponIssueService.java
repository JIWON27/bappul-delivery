package com.moduda.delivery.pomotion.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moduda.delivery.pomotion.application.event.consumer.FirstComeCouponIssueEvent;
import com.moduda.delivery.pomotion.application.validator.CouponValidator;
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
      FirstComeCouponIssueEvent evt = new FirstComeCouponIssueEvent(userId, couponPolicyId);
      UUID eventId = UUID.randomUUID();

      Message<String> msg = MessageBuilder
          .withPayload(objectMapper.writeValueAsString(evt))
          .setHeader(KafkaHeaders.TOPIC, "first-come-coupon-request")
          .setHeader(KafkaHeaders.KEY, String.valueOf(couponPolicyId))
          .setHeader("event-id", eventId.toString())
          .setHeader("event-type", "CouponIssueRequested")
          .setHeader("occurred-at", Instant.now().toString())
          .build();
      kafkaTemplate.send(msg);
    }catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
