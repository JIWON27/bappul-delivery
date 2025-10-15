package com.bappul.pomotion.application.mapper;

import com.bappul.pomotion.domain.entity.Coupon;
import com.bappul.pomotion.domain.entity.CouponPolicy;
import com.bappul.pomotion.domain.entity.CouponStatus;
import com.bappul.pomotion.domain.entity.CouponType;
import com.bappul.pomotion.web.v1.request.CouponPolicyRequest;
import com.bappul.pomotion.web.v1.response.CouponPolicyResponse;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CouponMapper {
  Coupon toCoupon(Long userId, CouponPolicy couponPolicy, CouponStatus status, CouponType type, String code, LocalDateTime expiresAt);
  CouponPolicy toCouponPolicy(CouponPolicyRequest request);
  CouponPolicyResponse toResponse(CouponPolicy couponPolicy);
}
