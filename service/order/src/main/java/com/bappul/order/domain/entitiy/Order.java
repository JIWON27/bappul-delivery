package com.bappul.order.domain.entitiy;


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

  @Column(name = "merchant_uid", nullable = false, unique = true)
  String merchantUid;

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
  OrderStatus orderStatus;

  @Column(name = "order_subtotal_price", nullable = false)
  BigDecimal orderSubtotalPrice; // 배달비, 할인 제외 가격

  @Column(name = "delivery_fee_price", nullable = false)
  BigDecimal deliveryFeePrice; // 배달비

  @Column(name = "order_discount_price", nullable = false)
  BigDecimal orderDiscountPrice; // 할인 금액

  @Column(name = "payable_total_price", nullable = false)
  BigDecimal payableTotalPrice; // 최종 결제금액

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Builder
  public Order(UUID orderNo, String idempotencyKey, String merchantUid, Long userId, Long addressId,
      Long storeId, Long couponId, OrderStatus orderStatus, BigDecimal orderSubtotalPrice,
      BigDecimal deliveryFeePrice, BigDecimal orderDiscountPrice, BigDecimal payableTotalPrice) {
    this.orderNo = orderNo;
    this.idempotencyKey = idempotencyKey;
    this.merchantUid = merchantUid;
    this.userId = userId;
    this.addressId = addressId;
    this.storeId = storeId;
    this.couponId = couponId;
    this.orderStatus = orderStatus;
    this.orderSubtotalPrice = orderSubtotalPrice;
    this.deliveryFeePrice = deliveryFeePrice;
    this.orderDiscountPrice = orderDiscountPrice;
    this.payableTotalPrice = payableTotalPrice;
  }

  public void markAsPaid() {
    this.orderStatus = OrderStatus.PAID;
  }

  public void markAsAccepted() {
    this.orderStatus = OrderStatus.ACCEPTED;
  }

  public void markAsRejected() {
    this.orderStatus = OrderStatus.REJECTED;
  }

  public void markAsReady() {
    this.orderStatus = OrderStatus.READY;
  }

  public void markAsPickUp() {
    this.orderStatus = OrderStatus.PICKED_UP;
  }

  public void markAsCompleted() {
    this.orderStatus = OrderStatus.COMPLETED;
  }

  public void markAsCanceled() {
    this.orderStatus = OrderStatus.CANCELED;
  }

  public void markAsRefunded() {
    this.orderStatus = OrderStatus.REFUNDED;
  }

  public void updateMerchantUid(String merchantUid) {
    this.merchantUid = merchantUid;
  }
}
