package com.bappul.pomotion.application.mapper;

import com.bappul.pomotion.domain.entity.CouponPolicy;
import com.bappul.pomotion.web.v1.request.CouponPolicyRequest;
import com.bappul.pomotion.web.v1.response.CouponPolicyResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CouponMapper {
  CouponPolicy toEntity(CouponPolicyRequest request);
  CouponPolicyResponse toResponse(CouponPolicy couponPolicy);
}
