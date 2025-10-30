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

  @Column(name = "payment_id", nullable = false)
  String paymentId;

  @Column(name = "merchant_uid", nullable = false)
  String merchantUid;

  @Column(name = "transaction_id", nullable = false)
  String transactionId;

  @Column(name = "pg_transaction_id", nullable = false)
  String pgTransactionId;

  @Enumerated(EnumType.STRING)
  @Column(name = "pg_provider", nullable = false)
  PgProvider pgProvider;

  @Enumerated(EnumType.STRING)
  @Column(name = "pay_method", nullable = false)
  PayMethod payMethod;

  @Column(name = "approved_price", nullable = false)
  BigDecimal approvedPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "currency", nullable = false)
  Currency currency;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  PaymentStatus status;

  @Column(name = "receipt_url")
  String receiptUrl;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Column(name = "paid_at")
  LocalDateTime paidAt;

  @Column(name = "canceled_at")
  LocalDateTime canceledAt;

  @Builder
  public Payment(Long orderId, String paymentId, String merchantUid, String transactionId,
      String pgTransactionId, PgProvider pgProvider, PayMethod payMethod, BigDecimal approvedPrice,
      Currency currency, PaymentStatus status, String receiptUrl, LocalDateTime paidAt) {
    this.orderId = orderId;
    this.paymentId = paymentId;
    this.merchantUid = merchantUid;
    this.transactionId = transactionId;
    this.pgTransactionId = pgTransactionId;
    this.pgProvider = pgProvider;
    this.payMethod = payMethod;
    this.approvedPrice = approvedPrice;
    this.currency = currency;
    this.status = status;
    this.receiptUrl = receiptUrl;
    this.paidAt = paidAt;
  }

  public void markAsPaid() {
    this.status = PaymentStatus.PAID;
  }

  public void markAsFail() {
    this.status = PaymentStatus.FAILED;
  }

  public void markAsRefunded() {
    this.status = PaymentStatus.REFUNDED;
  }
}
