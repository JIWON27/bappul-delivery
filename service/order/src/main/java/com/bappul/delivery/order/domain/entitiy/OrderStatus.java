package com.bappul.delivery.order.domain.entitiy;

public enum OrderStatus {
  CREATED,     // 주문 생성(결제 전)
  PAID,        // 결제 성공(결제 웹훅/이벤트 수신)
  ACCEPTED,    // 점주 수락
  REJECTED,    // 점주 거절
  READY,       // 조리 완료
  COMPLETED,   // 수령/배달 완료
  CANCELED,    // 결제 전 사용자 취소
  REFUNDED     // 결제 후 환불 완료
}
