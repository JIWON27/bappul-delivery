package com.bappul.delivery.catalog.domain.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Table(name = "stores")
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE stores SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Store {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "user_id", nullable = false)
  Long userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  Category category;

  @Column(name = "name", nullable = false, length = 100)
  String name;

  @Column(name = "phone_number", nullable = false, length = 20)
  String phoneNumber;

  @Column(name = "address",  nullable = false, length = 100)
  String address;

  @Column(name = "introduction",  nullable = false, length = 255)
  String introduction;

  @Column(name="min_order_price", nullable=false, precision=10, scale=0)
  BigDecimal minOrderPrice;

  @Column(name="delivery_fee", nullable=false, precision=10, scale=0)
  BigDecimal deliveryFee;

  @Column(name = "open_status", nullable = false)
  Boolean openStatus = false;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  LocalDateTime deletedAt;

  @Builder
  public Store(Long userId, Category category, String name, String phoneNumber, String address,
      String introduction, BigDecimal minOrderPrice, BigDecimal deliveryFee, Boolean openStatus) {
    this.userId = userId;
    this.category = category;
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.address = address;
    this.introduction = introduction;
    this.minOrderPrice = minOrderPrice;
    this.deliveryFee = deliveryFee;
    this.openStatus = openStatus;
  }

  public void updateOpenStatus(Boolean openStatus) {
    this.openStatus = openStatus;
  }
}
