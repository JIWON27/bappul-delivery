package com.moduda.delivery.pomotion.application.mapper;

import com.moduda.delivery.pomotion.domain.entity.CouponPolicy;
import com.moduda.delivery.pomotion.web.v1.request.CouponPolicyRequest;
import com.moduda.delivery.pomotion.web.v1.response.CouponPolicyResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CouponMapper {
  CouponPolicy toEntity(CouponPolicyRequest request);
  CouponPolicyResponse toResponse(CouponPolicy couponPolicy);
}
