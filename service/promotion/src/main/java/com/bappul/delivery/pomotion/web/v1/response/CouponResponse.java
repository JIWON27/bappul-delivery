package com.bappul.delivery.pomotion.web.v1.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.bappul.delivery.pomotion.domain.entity.Coupon;
import com.bappul.delivery.pomotion.domain.entity.CouponPolicy;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponResponse {

  Long couponId;
  String couponName;
  int discountValue;
  BigDecimal minOrderPrice;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  BigDecimal maxDiscountPrice;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH::mm::ss")
  LocalDateTime expiresAt;
  int dDay;
  boolean expired;

  public static CouponResponse of(Coupon coupon, CouponPolicy policy, int dDay, boolean expired){
    return CouponResponse.builder()
        .couponId(coupon.getId())
        .couponName(policy.getCouponName())
        .discountValue(policy.getDiscountValue())
        .minOrderPrice(policy.getMinOrderPrice())
        .maxDiscountPrice(policy.getMaxDiscountPrice())
        .expiresAt(coupon.getExpiresAt())
        .dDay(dDay)
        .expired(expired)
        .build();
  }
}
