package com.moduda.delivery.pomotion.web.v1.request;

import com.moduda.delivery.pomotion.domain.entity.ActiveStatus;
import com.moduda.delivery.pomotion.domain.entity.DiscountType;
import com.moduda.delivery.pomotion.domain.entity.ExpirationType;
import com.moduda.delivery.pomotion.domain.entity.IssueMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponPolicyRequest {

  @NotBlank(message = "쿠폰 이름을 입력해주세요.")
  String couponName;

  @NotNull(message = "쿠폰 할인 유형을 입력해주세요.")
  DiscountType discountType;

  @NotNull(message = "쿠폰 만기 유형을 입력해주세요")
  ExpirationType expirationType;

  @NotNull(message = "쿠폰 발급 유형을 입력해주세요")
  IssueMode issueMode;

  @NotNull(message = "할인 값을 입력해주세요.")
  BigDecimal discountValue;

  @NotNull(message = "최소 주문 금액을 입력해주세요.")
  BigDecimal minOrderPrice;

  @NotNull(message = "최대 할인 금액을 입력해주세요.")
  BigDecimal maxDiscountPrice;

  Integer validDays;
  LocalDateTime startDate;
  LocalDateTime endDate;

  @NotNull(message = "사용 제한 횟수를 입력해주세요.")
  Integer perUserLimit;
  Integer totalQuantity;

  @NotNull(message = "쿠폰 활성 상태를 입력해주세요")
  ActiveStatus activeStatus;
}
