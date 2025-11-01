package com.bappul.catalog.domain.entity;


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

  @Column(name = "phone", nullable = false, length = 20)
  String phone;

  @Column(name = "road_address", nullable = false)
  String roadAddress;

  @Column(name = "detail_address", nullable = false)
  String detailAddress;

  @Column(name = "zipcode", nullable = false)
  String zipcode;

  @Column(name = "jibun_address")
  String jibunAddress;

  @Column(name = "haengjeong_code", nullable = false)
  String haengjeongCode;

  @Column(name = "beopjeong_Code", nullable = false)
  String beopjeongCode;

  @Column(name = "longitude", nullable = false)
  Double longitude;

  @Column(name = "latitude", nullable = false)
  Double latitude;

  @Column(name = "introduction",  nullable = false, length = 255)
  String introduction;

  @Column(name="min_order_price", nullable=false, precision=10, scale=0)
  BigDecimal minOrderPrice;

  @Column(name="delivery_fee", nullable=false, precision=10, scale=0)
  BigDecimal deliveryFee;

  @Column(name = "open_status", nullable = false)
  Boolean openStatus;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  LocalDateTime deletedAt;

  @Builder
  public Store(Long userId, Category category, String name, String phone, String roadAddress,
      String detailAddress, String zipcode, String jibunAddress, String haengjeongCode,
      String beopjeongCode, Double longitude, Double latitude, String introduction,
      BigDecimal minOrderPrice, BigDecimal deliveryFee, Boolean openStatus) {
    this.userId = userId;
    this.category = category;
    this.name = name;
    this.phone = phone;
    this.roadAddress = roadAddress;
    this.detailAddress = detailAddress;
    this.zipcode = zipcode;
    this.jibunAddress = jibunAddress;
    this.haengjeongCode = haengjeongCode;
    this.beopjeongCode = beopjeongCode;
    this.longitude = longitude;
    this.latitude = latitude;
    this.introduction = introduction;
    this.minOrderPrice = minOrderPrice;
    this.deliveryFee = deliveryFee;
    this.openStatus = openStatus;
  }

  public void updateOpenStatus(Boolean openStatus) {
    this.openStatus = openStatus;
  }
}
