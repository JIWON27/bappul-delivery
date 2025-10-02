package com.bappul.delivery.order.domain.entitiy;


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
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

@Table(name = "orders")
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "order_no", nullable = false, unique = true)
  UUID orderNo;

  @Column(name = "idempotency_key", nullable = false, unique = true)
  String idempotencyKey;

  @Column(name = "user_id", nullable = false)
  Long userId;

  @Column(name = "address_id", nullable = false)
  Long addressId;

  @Column(name = "store_id", nullable = false)
  Long storeId;

  @Column(name = "coupon_id")
  Long couponId;

  @Enumerated(EnumType.STRING)
  @Column(name = "order_status", nullable = false, length = 30)
  OrderStatus orderStatus; //

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", nullable = false, length = 30)
  PaymentStatus paymentStatus;

  @Enumerated(EnumType.STRING)
  @Column(name = "delivery_status", nullable = false, length = 30)
  DeliveryStatus deliveryStatus;

  @Column(name = "line_total", nullable = false)
  BigDecimal lineTotal; // 해당 주문 총 가격(배달비 제외)

  @Column(name = "delivery_fee", nullable = false)
  BigDecimal deliveryFee; // 배달비

  @Column(name = "order_discount", nullable = false)
  BigDecimal orderDiscount; // 할인 금액

  @Column(name = "payable_total", nullable = false)
  BigDecimal payableTotal; // 결제 총 금액 = lineTotal + deliveryFee

  @Column(name = "cancel_reason", length = 200)
  String cancelReason;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Builder
  public Order(UUID orderNo, String idempotencyKey, Long userId, Long addressId, Long storeId,
      Long couponId, OrderStatus orderStatus, PaymentStatus paymentStatus,
      DeliveryStatus deliveryStatus, BigDecimal lineTotal, BigDecimal deliveryFee,
      BigDecimal orderDiscount, BigDecimal payableTotal, String cancelReason) {
    this.orderNo = orderNo;
    this.idempotencyKey = idempotencyKey;
    this.userId = userId;
    this.addressId = addressId;
    this.storeId = storeId;
    this.couponId = couponId;
    this.orderStatus = orderStatus;
    this.paymentStatus = paymentStatus;
    this.deliveryStatus = deliveryStatus;
    this.lineTotal = lineTotal;
    this.deliveryFee = deliveryFee;
    this.orderDiscount = orderDiscount;
    this.payableTotal = payableTotal;
    this.cancelReason = cancelReason;
  }

  public void markAsCanceled() {
    this.orderStatus = OrderStatus.CANCELED;
  }
}
