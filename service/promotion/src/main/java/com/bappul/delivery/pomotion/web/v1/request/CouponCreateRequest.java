package com.bappul.delivery.pomotion.web.v1.request;

import com.bappul.delivery.pomotion.domain.entity.CouponType;
import com.bappul.delivery.pomotion.domain.entity.IssueMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponCreateRequest {
  @NotNull(message = "쿠폰 정책 ID는 필수입니다.")
  Long couponPolicyId;

  @Min(value = 1, message = "발급 수량은 최소 1개 이상이어야 합니다.")
  @Max(value = 1000, message = "발급 수량은 최대 1000개까지 가능합니다.")
  int quantity;

  @Pattern(
      regexp = "^$|^[A-Z0-9-]{5,50}$",
      message = "대문자, 숫자, 하이픈(-)만 사용하며 길이는 5~50자여야 합니다."
  )
  String prefix;

  @NotNull(message = "쿠폰 유형을 입력해주세요.")
  CouponType type;

  @NotNull(message = "쿠폰 발급 모드를 입력해주세요.")
  IssueMode issueMode;
}
