package com.bappul.delivery.cart.domain.entity;

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

@Table(name = "carts")
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "store_id", nullable = false)
  Long storeId;

  @Column(name = "store_name", nullable = false)
  String storeName;

  @Column(name = "user_id", nullable = false)
  Long userId;

  @Column(name = "total_price")
  BigDecimal totalPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  Status status;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Builder
  public Cart(Long storeId, String storeName, Long userId, BigDecimal totalPrice, Status status) {
    this.storeId = storeId;
    this.storeName = storeName;
    this.userId = userId;
    this.totalPrice = totalPrice;
    this.status = status;
  }

}

