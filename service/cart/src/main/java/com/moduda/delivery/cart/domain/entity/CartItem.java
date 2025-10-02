package com.moduda.delivery.cart.domain.entity;

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

@Table(name = "cart_items")
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_id", nullable = false)
  Cart cart;

  @Column(name = "menu_id", nullable = false)
  Long menuId;

  @Column(name = "menu_name", nullable = false)
  String menuName;

  @Column(name = "quantity", nullable = false)
  int quantity;

  @Column(name = "base_price_snapshot", nullable = false)
  BigDecimal basePriceSnapshot;

  @Column(name = "option_price_snapshot", nullable = false)
  BigDecimal optionPriceSnapshot;

  @Column(name = "unit_price_snapshot", nullable = false)
  BigDecimal unitPriceSnapshot;

  @Column(name = "line_total_snapshot", nullable = false)
  BigDecimal lineTotalSnapshot;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Builder
  public CartItem(Cart cart, Long menuId, String menuName, int quantity,
      BigDecimal basePriceSnapshot,
      BigDecimal optionPriceSnapshot, BigDecimal unitPriceSnapshot, BigDecimal lineTotalSnapshot) {
    this.cart = cart;
    this.menuId = menuId;
    this.menuName = menuName;
    this.quantity = quantity;
    this.basePriceSnapshot = basePriceSnapshot;
    this.optionPriceSnapshot = optionPriceSnapshot;
    this.unitPriceSnapshot = unitPriceSnapshot;
    this.lineTotalSnapshot = lineTotalSnapshot;
  }
}
