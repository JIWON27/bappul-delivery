package com.moduda.delivery.pomotion.domain.entity;

public enum ExpirationType {
  COUPON_CREATED, // 쿠폰 생성일
  FIXED_PERIOD,   // 고정 기간 start_date ~ end_date
  ISSUE_RELATIVE  // 발급일로부터 ~일
  ;
}
