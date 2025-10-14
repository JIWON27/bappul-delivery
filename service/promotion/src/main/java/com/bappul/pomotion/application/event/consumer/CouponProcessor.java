package com.bappul.pomotion.application.event.consumer;

import static com.bappul.pomotion.exception.ServiceExceptionCode.JSON_DESERIALIZATION_ERROR;

import com.bappul.pomotion.application.event.contracts.coupon.CouponEventPayload;
import com.bappul.pomotion.application.mapper.CouponMapper;
import com.bappul.pomotion.application.service.ExpirationCalculator;
import com.bappul.pomotion.application.utils.TimeUtils;
import com.bappul.pomotion.application.validator.CouponValidator;
import com.bappul.pomotion.domain.entity.Coupon;
import com.bappul.pomotion.domain.entity.CouponPolicy;
import com.bappul.pomotion.domain.entity.CouponStatus;
import com.bappul.pomotion.domain.entity.CouponType;
import com.bappul.pomotion.domain.repository.CouponRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ServiceException;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponProcessor {

  private final CouponRepository couponRepository;
  private final ObjectMapper objectMapper;
  private final CouponValidator couponValidator;
  private final CouponMapper couponMapper;
  private final ExpirationCalculator expirationCalculator;
  private final TimeUtils timeUtils;

  @Transactional
  public void processCouponUsed(String payload) {
    try {
      CouponEventPayload couponUsedRequest = objectMapper.readValue(payload, CouponEventPayload.class);

      if (!Objects.isNull(couponUsedRequest.getCouponId())) {
        Coupon coupon = couponValidator.getCoupon(couponUsedRequest.getCouponId());
        CouponPolicy couponPolicy = coupon.getCouponPolicy();

        if (coupon.getStatus() == CouponStatus.USED) {
          return;
        }

        coupon.markAsUsed(timeUtils.now());
        couponPolicy.incrementRedeemedQuantity();
      }
    } catch (JsonProcessingException e) {
      throw new ServiceException(JSON_DESERIALIZATION_ERROR);
    }
  }

  @Transactional
  public void processCouponRollback(String payload){
    try {
      CouponEventPayload couponUsedRequest = objectMapper.readValue(payload, CouponEventPayload.class);
      Coupon coupon = couponValidator.getCoupon(couponUsedRequest.getCouponId());
      CouponPolicy couponPolicy = coupon.getCouponPolicy();

      if (coupon.getStatus() == CouponStatus.CANCELLED) {
        return;
      }

      coupon.markAsCancelled();
      couponPolicy.decreaseRedeemedQuantity();
    } catch (JsonProcessingException e) {
      throw new ServiceException(JSON_DESERIALIZATION_ERROR);
    }
  }

  @Transactional
  public void processFirstCouponIssue(String payload){
    try {
      FirstComeCouponIssueEvent event = objectMapper.readValue(payload, FirstComeCouponIssueEvent.class);
      CouponPolicy couponPolicy = couponValidator.getCouponPolicy(event.getCouponPolicyId());
      // 쿠폰 생성 전 수량 체크
      couponValidator.validateCouponIssueLimit(couponPolicy.getIssuedQuantity(), 1, couponPolicy.getTotalQuantity());

      // 쿠폰 생성
      LocalDateTime expiresAt = expirationCalculator.computeExpiresAt(couponPolicy);
      Coupon coupon = couponMapper.toCoupon(event.getUserId(), couponPolicy, CouponStatus.CREATED, CouponType.ONLINE, null, expiresAt);
      coupon.markAsIssued(timeUtils.now());
      couponRepository.save(coupon);

    } catch (JsonProcessingException e) {
      throw new ServiceException(JSON_DESERIALIZATION_ERROR);
    }
  }
}
