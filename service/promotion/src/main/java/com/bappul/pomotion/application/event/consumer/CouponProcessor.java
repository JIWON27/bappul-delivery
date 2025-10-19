package com.bappul.pomotion.application.event.consumer;

import static com.bappul.pomotion.exception.ServiceExceptionCode.EVENT_ID_MISSING;
import static com.bappul.pomotion.exception.ServiceExceptionCode.EVENT_PAYLOAD_MISSING;
import static com.bappul.pomotion.exception.ServiceExceptionCode.EVENT_PROCESSING_FAILED;
import static com.bappul.pomotion.exception.ServiceExceptionCode.EVENT_TYPE_MISSING;
import static com.bappul.pomotion.exception.ServiceExceptionCode.JSON_DESERIALIZATION_ERROR;

import com.bappul.pomotion.application.event.contracts.coupon.CouponEvent;
import com.bappul.pomotion.application.event.contracts.coupon.FirstComeCouponIssueEvent;
import com.bappul.pomotion.application.mapper.CouponMapper;
import com.bappul.pomotion.application.service.ExpirationCalculator;
import com.bappul.pomotion.application.utils.TimeUtils;
import com.bappul.pomotion.application.validator.CouponValidator;
import com.bappul.pomotion.domain.entity.Coupon;
import com.bappul.pomotion.domain.entity.CouponPolicy;
import com.bappul.pomotion.domain.entity.CouponStatus;
import com.bappul.pomotion.domain.entity.CouponType;
import com.bappul.pomotion.domain.entity.InboxStatus;
import com.bappul.pomotion.domain.repository.CouponRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ServiceException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponProcessor {

  private final CouponRepository couponRepository;
  private final CouponValidator couponValidator;
  private final InboxManager inboxManager;
  private final TimeUtils timeUtils;
  private final CouponMapper couponMapper;
  private final ExpirationCalculator expirationCalculator;
  private final ObjectMapper objectMapper;

  @Transactional
  public void processCouponUsed(ConsumerRecord<String, String> record) {
    handleEvent(record, CouponEvent.class, this::processCouponUsedLogic);
  }

  @Transactional
  public void processCouponRollback(ConsumerRecord<String, String> record){
    handleEvent(record, CouponEvent.class, this::processCouponRollbackLogic);
  }

  @Transactional
  public void processFirstCouponIssue(ConsumerRecord<String, String> record){
    handleEvent(record, FirstComeCouponIssueEvent.class, this::processFirstCouponIssueLogic);
  }

  private void processCouponUsedLogic(CouponEvent event) throws Exception {
    if (!Objects.isNull(event.getCouponId())) {
      Coupon coupon = couponValidator.getCoupon(event.getCouponId());
      CouponPolicy couponPolicy = coupon.getCouponPolicy();

      if (coupon.getStatus() == CouponStatus.USED) {
        return;
      }

      coupon.markAsUsed(timeUtils.now());
      couponPolicy.incrementRedeemedQuantity();
    }
  }
  private void processCouponRollbackLogic (CouponEvent event) throws Exception {
    Coupon coupon = couponValidator.getCoupon(event.getCouponId());
    CouponPolicy couponPolicy = coupon.getCouponPolicy();

    if (coupon.getStatus() == CouponStatus.CANCELLED) {
      return;
    }

    coupon.markAsCancelled();
    couponPolicy.decreaseRedeemedQuantity();
  }

  private void processFirstCouponIssueLogic (FirstComeCouponIssueEvent event) throws Exception {
    CouponPolicy couponPolicy = couponValidator.getCouponPolicy(event.getCouponPolicyId());
    // 쿠폰 생성 전 수량 체크
    couponValidator.validateCouponIssueLimit(couponPolicy.getIssuedQuantity(), 1,
        couponPolicy.getTotalQuantity());

    // 쿠폰 생성
    LocalDateTime expiresAt = expirationCalculator.computeExpiresAt(couponPolicy);
    Coupon coupon = couponMapper.toCoupon(event.getUserId(), couponPolicy, CouponStatus.CREATED,
        CouponType.ONLINE, null, expiresAt);

    coupon.markAsIssued(timeUtils.now());
    couponRepository.save(coupon);
  }

  private <T > void handleEvent(ConsumerRecord < String, String > record, Class <T> clazz, ThrowingExceptionConsumer < T> consumer){
    String eventId = parseHeader(record, "event-id");
    String eventType = parseHeader(record, "event-type");
    String payload = record.value();

    checkRequiredEventFields(eventId, eventType, payload);
    if (!inboxManager.idempotencyGuard(eventId, eventType, payload)) {
      log.info("[inbox] duplicate eventId={}, skip", eventId);
      return;
    }

    try {
      T event = parse(payload, clazz);
      consumer.accept(event);
      inboxManager.inboxWriter(eventId, InboxStatus.PROCESSED);
    } catch (JsonProcessingException e) {
      inboxManager.inboxWriter(eventId, InboxStatus.FAILED);
      throw new ServiceException(JSON_DESERIALIZATION_ERROR);
    } catch (Exception e) {
      inboxManager.inboxWriter(eventId, InboxStatus.FAILED);
      throw new ServiceException(EVENT_PROCESSING_FAILED);
    }
  }

  private <T> T parse(String payload, Class < T > clazz) throws JsonProcessingException {
    return objectMapper.readValue(payload, clazz);
  }

  private String parseHeader (ConsumerRecord < String, String > record, String headerKey){
    Header header = record.headers().lastHeader(headerKey);
    return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
  }

  private void checkRequiredEventFields(String eventId, String eventType, String payload) {
    if (eventId == null || eventId.isBlank()) {
      throw new ServiceException(EVENT_ID_MISSING);
    }
    if (eventType == null || eventType.isBlank()) {
      throw new ServiceException(EVENT_TYPE_MISSING);
    }
    if (payload == null || payload.isBlank()) {
      throw new ServiceException(EVENT_PAYLOAD_MISSING);
    }
  }
}
