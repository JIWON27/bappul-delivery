package com.moduda.delivery.pomotion.application.event.consumer;

import static com.moduda.delivery.pomotion.exception.ServiceExceptionCode.JSON_DESERIALIZATION_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moduda.delivery.pomotion.application.event.contracts.coupon.CouponEventPayload;
import com.moduda.delivery.pomotion.application.service.ExpirationCalculator;
import com.moduda.delivery.pomotion.application.validator.CouponValidator;
import com.moduda.delivery.pomotion.domain.entity.Coupon;
import com.moduda.delivery.pomotion.domain.entity.CouponPolicy;
import com.moduda.delivery.pomotion.domain.entity.CouponStatus;
import com.moduda.delivery.pomotion.domain.entity.CouponType;
import com.moduda.delivery.pomotion.domain.repository.CouponRepository;
import exception.ServiceException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponProcessor {

  private final ObjectMapper objectMapper;
  private final CouponValidator couponValidator;
  private final ExpirationCalculator expirationCalculator;
  private final CouponRepository couponRepository;

  @Transactional
  public void processCouponUsed(String payload) {
    try {
      CouponEventPayload couponUsedRequest = objectMapper.readValue(payload, CouponEventPayload.class);
      Coupon coupon = couponValidator.getCoupon(couponUsedRequest.getCouponId());
      CouponPolicy couponPolicy = coupon.getCouponPolicy();

      if (coupon.getStatus() == CouponStatus.USED) {
        return;
      }

      coupon.markAsUsed();
      couponPolicy.incrementRedeemedQuantity();
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
      LocalDateTime expiresAt = expirationCalculator.getExpiresAt(couponPolicy);
      Coupon coupon = Coupon.builder()
          .userId(event.getUserId())
          .couponPolicy(couponPolicy)
          .status(CouponStatus.CREATED)
          .type(CouponType.ONLINE)
          .code(null)
          .expiresAt(expiresAt)
          .issuedAt(LocalDateTime.now())
          .build();
      couponRepository.save(coupon);

    } catch (JsonProcessingException e) {
      throw new ServiceException(JSON_DESERIALIZATION_ERROR);
    }
  }
}
