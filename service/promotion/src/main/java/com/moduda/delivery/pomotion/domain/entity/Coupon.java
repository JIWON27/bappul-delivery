package com.moduda.delivery.pomotion.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

@Table(name = "coupons")
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Coupon {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "code", unique = true, length = 40)
  String code;

  @Column(name = "user_id", nullable = true)
  Long userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coupon_policy_id", nullable = false)
  CouponPolicy couponPolicy;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  CouponStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  CouponType type;

  // 유저에게 발급/등록(Claim)된 순간
  @Column(name = "issued_at")
  LocalDateTime issuedAt;

  // 발급/생성/고정기간 기준으로 '그 시점에 확정'해서 저장
  @Column(name = "expires_at", nullable = false)
  LocalDateTime expiresAt;

  @Column(name = "used_at")
  LocalDateTime usedAt;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;


  public void updateUser(Long userId){
    this.userId = userId;
  }

  public void markAsIssued(){
    this.status = CouponStatus.ISSUED;
  }

  public void markAsUsed(){
    this.status = CouponStatus.USED;
    this.usedAt = LocalDateTime.now();
  }

  public void markAsCancelled(){
    this.status = CouponStatus.ISSUED;
  }

  public void updateExpiresAt(LocalDateTime expiresAt){
    this.expiresAt = expiresAt;
  }

  @Builder
  public Coupon(String code, Long userId, CouponPolicy couponPolicy, CouponStatus status, CouponType type, LocalDateTime issuedAt, LocalDateTime expiresAt) {
    this.code = code;
    this.userId = userId;
    this.couponPolicy = couponPolicy;
    this.status = status;
    this.type = type;
    this.issuedAt = issuedAt;
    this.expiresAt = expiresAt;
  }
}
