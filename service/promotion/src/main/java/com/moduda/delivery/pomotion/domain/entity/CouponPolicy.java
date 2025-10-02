package com.moduda.delivery.pomotion.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

@Table(name = "coupon_policies")
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponPolicy {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "coupon_name", nullable = false)
  String couponName;

  @Enumerated(EnumType.STRING)
  @Column(name = "discount_type", nullable = false)
  DiscountType discountType; // 정액/정률

  @Column(name = "discount_value", nullable = false)
  int discountValue; // 할인 크기

  @Column(name = "min_order_price", precision = 10, scale = 0)
  BigDecimal minOrderPrice; // 최소 주문 금액

  @Column(name = "max_discount_Price", precision = 10, scale = 0)
  BigDecimal maxDiscountPrice; // 최대 할인 금액

  @Enumerated(EnumType.STRING)
  @Column(name = "expiration_type", nullable = false)
  ExpirationType expirationType; // 만료 기간 종류

  @Enumerated(EnumType.STRING)
  @Column(name = "issue_mode", nullable = false)
  IssueMode issueMode; // 쿠폰 유형(선착순, 일반)

  @Column(name = "valid_days")
  Integer validDays; // 생성일로부 ~일 쿠폰 만료 기간 계산을 위해서,

  @Column(name = "start_date")
  LocalDateTime startDate; // 시작일

  @Column(name = "end_date")
  LocalDateTime endDate; // 종료일

  @Column(name = "per_user_limit", nullable = false)
  int perUserLimit = 1; // 사용자별 발급장수 제한

  @Column(name = "total_quantity")
  Integer totalQuantity; // 총 발행 가능한 쿠폰 개수,

  @Column(name = "issued_quantity", nullable = false)
  int issuedQuantity = 0; // 현재 발행 개수

  @Column(name = "redeemed_quantity", nullable = false)
  int redeemedQuantity = 0; // 사용 개수.

  @Enumerated(EnumType.STRING)
  @Column(name = "active_status", nullable = false)
  ActiveStatus activeStatus; // 쿠폰 정책 활성화

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Builder
  public CouponPolicy(String couponName, DiscountType discountType, ExpirationType expirationType,
      IssueMode issueMode, int discountValue, BigDecimal minOrderPrice, BigDecimal maxDiscountPrice,
      Integer validDays, LocalDateTime startDate, LocalDateTime endDate, Integer perUserLimit,
      Integer totalQuantity, int issuedQuantity, int redeemedQuantity, ActiveStatus activeStatus) {
    this.couponName = couponName;
    this.discountType = discountType;
    this.expirationType = expirationType;
    this.issueMode = issueMode;
    this.discountValue = discountValue;
    this.minOrderPrice = minOrderPrice;
    this.maxDiscountPrice = maxDiscountPrice;
    this.validDays = validDays;
    this.startDate = startDate;
    this.endDate = endDate;
    this.perUserLimit = perUserLimit;
    this.totalQuantity = totalQuantity;
    this.issuedQuantity = issuedQuantity;
    this.redeemedQuantity = redeemedQuantity;
    this.activeStatus = activeStatus;
  }

  public Boolean isActive() {
    return activeStatus == ActiveStatus.ACTIVE;
  }

  public void incrementIssuedQuantity() {
    this.issuedQuantity += 1;
  }

  public void incrementRedeemedQuantity(){
    this.redeemedQuantity += 1;
  }

  public void decreaseRedeemedQuantity(){
    this.redeemedQuantity -= 1;
  }

}
