package com.bappul.delivery.payment.domain.entity;

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

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "payment")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "order_id", nullable = false)
  Long orderId;

  @Column(name = "user_id", nullable = false)
  Long userId;

  @Column(name = "price", nullable = false)
  BigDecimal price;

  @Column(name = "imp_uid", nullable = false)
  String impUid;

  @Column(name = "merchant_uid", nullable = false)
  String merchantUid;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  PaymentStatus status;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Builder
  public Payment(Long orderId, Long userId, BigDecimal price, String impUid, String merchantUid,
      PaymentStatus status) {
    this.orderId = orderId;
    this.userId = userId;
    this.price = price;
    this.impUid = impUid;
    this.merchantUid = merchantUid;
    this.status = status;
  }

  public void updateImpUid(String impUid) {
    this.impUid = impUid;
  }

  public void markAsRefunded() {
    this.status = PaymentStatus.REFUNDED;
  }

  public void markAsPaid() {
    this.status = PaymentStatus.PAID;
  }

  public void markAsFail() {
    this.status = PaymentStatus.FAIL;
  }
}
