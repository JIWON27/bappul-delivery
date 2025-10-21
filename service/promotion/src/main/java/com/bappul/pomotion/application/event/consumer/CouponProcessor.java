package com.bappul.pomotion.application.event.consumer;

import com.bappul.event.kafka.KafkaEventProcessor;
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
import com.bappul.pomotion.domain.repository.CouponRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponProcessor {

  private final CouponRepository couponRepository;
  private final CouponValidator couponValidator;
  private final KafkaEventProcessor eventProcessor;
  private final TimeUtils timeUtils;
  private final CouponMapper couponMapper;
  private final ExpirationCalculator expirationCalculator;

  @Transactional
  public void processCouponUsed(ConsumerRecord<String, String> record) {
    eventProcessor.handleEvent(record, CouponEvent.class, this::processCouponUsedLogic);
  }

  @Transactional
  public void processCouponRollback(ConsumerRecord<String, String> record){
    eventProcessor.handleEvent(record, CouponEvent.class, this::processCouponRollbackLogic);
  }

  @Transactional
  public void processFirstCouponIssue(ConsumerRecord<String, String> record){
    eventProcessor.handleEvent(record, FirstComeCouponIssueEvent.class, this::processFirstCouponIssueLogic);
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
}
