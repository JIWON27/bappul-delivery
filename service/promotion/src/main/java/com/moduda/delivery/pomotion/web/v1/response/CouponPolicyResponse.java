package com.moduda.delivery.pomotion.web.v1.response;

import com.moduda.delivery.pomotion.domain.entity.ActiveStatus;
import com.moduda.delivery.pomotion.domain.entity.DiscountType;
import com.moduda.delivery.pomotion.domain.entity.ExpirationType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponPolicyResponse {
    Long id;
    String couponName;
    DiscountType discountType;
    BigDecimal discountValue;
    BigDecimal minOrderPrice;
    BigDecimal maxDiscountPrice;
    ExpirationType expirationType;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Integer validDays;
    Integer perUserLimit;
    Integer totalQuantity;
    ActiveStatus activeStatus;
}
