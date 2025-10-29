package com.bappul.order.domain.entitiy;

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
import org.hibernate.annotations.UpdateTimestamp;

@Table(name = "order_item")
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  Order order;

  @Column(name = "menu_id", nullable = false)
  Long menuId;

  @Column(name = "menu_name")
  String menuName;

  @Column(name = "base_price")
  BigDecimal basePrice; // 메뉴 단품 값

  @Column(name = "unit_price")
  BigDecimal unitPrice; // 메뉴 단품 + 메뉴 옵션 값

  @Column(name = "line_total_price")
  BigDecimal lineTotalPrice; // (음식 단품 값 + 옵션 값) * 수량

  @Column(name = "line_discount_price")
  BigDecimal lineDiscountPrice; // 음식에 들어간 할인 가격

  @Column(name = "quantity")
  int quantity;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Builder
  public OrderItem(Order order, Long menuId, String menuName, BigDecimal basePrice, BigDecimal unitPrice, BigDecimal lineTotalPrice, BigDecimal lineDiscountPrice, int quantity) {
    this.order = order;
    this.menuId = menuId;
    this.menuName = menuName;
    this.basePrice = basePrice;
    this.unitPrice = unitPrice;
    this.lineTotalPrice = lineTotalPrice;
    this.lineDiscountPrice = lineDiscountPrice;
    this.quantity = quantity;
  }
}
