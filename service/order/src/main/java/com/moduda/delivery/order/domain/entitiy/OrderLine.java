package com.moduda.delivery.order.domain.entitiy;

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

@Table(name = "order_line")
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderLine {

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
  BigDecimal basePrice; // 음식 단품 값

  @Column(name = "unit_price")
  BigDecimal unitPrice; // 음식 단품 값 + 옵션 값

  @Column(name = "line_total")
  BigDecimal lineTotal; // (음식 단품 값 + 옵션 값) * 수량

  @Column(name = "line_discount")
  BigDecimal lineDiscount; // 음식에 들어간 할인 가격

  @Column(name = "refunded_price")
  BigDecimal refundedPrice; // 환불해줄때의 가격 = line_total_price - line_discount

  @Column(name = "quantity")
  int quantity;

  @Column(name = "refunded_quantity")
  int refundedQuantity;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Builder
  public OrderLine(Order order, Long menuId, String menuName, BigDecimal basePrice,
      BigDecimal unitPrice, BigDecimal lineTotal, BigDecimal lineDiscount, BigDecimal refundedPrice,
      int quantity, int refundedQuantity) {
    this.order = order;
    this.menuId = menuId;
    this.menuName = menuName;
    this.basePrice = basePrice;
    this.unitPrice = unitPrice;
    this.lineTotal = lineTotal;
    this.lineDiscount = lineDiscount;
    this.refundedPrice = refundedPrice;
    this.quantity = quantity;
    this.refundedQuantity = refundedQuantity;
  }
}
