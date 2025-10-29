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
@Table(name = "payment_intent")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentIntent {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "order_id", nullable = false)
  Long orderId;

  @Column(name = "order_name", length = 100, nullable = false)
  String orderName;

  @Column(name = "payment_id")
  String paymentId;

  @Column(name = "expected_price", nullable = false, precision = 10, scale = 2)
  BigDecimal expectedPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "currency", nullable = false)
  Currency currency;

  @Column(name = "channel_key")
  String channelKey;

  @Enumerated(EnumType.STRING)
  @Column(name = "pay_method", nullable = false)
  PayMethod payMethod;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Builder
  public PaymentIntent(Long orderId, String orderName, String paymentId, BigDecimal expectedPrice, Currency currency,
      String channelKey, PayMethod payMethod) {
    this.orderId = orderId;
    this.orderName = orderName;
    this.paymentId = paymentId;
    this.expectedPrice = expectedPrice;
    this.currency = currency;
    this.channelKey = channelKey;
    this.payMethod = payMethod;
  }

  public void updatePaymentId(String paymentId) {
    this.paymentId = paymentId;
  }
}
