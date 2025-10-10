package com.bappul.delivery.domain.entity;

public enum DeliveryStatus {
  REQUESTED,   // 배달 요청만 생성(라이더 미정으로 riderId = null)
  ASSIGNED,    // 라이더 배차 확정(riderId != null)
  PICKED_UP,   // 라이더가 매장에서 픽업
  DELIVERED,   // 배달 완료
  CANCELLED,  // 취소
  FAILED      // 비정상 종료
  ;
}
