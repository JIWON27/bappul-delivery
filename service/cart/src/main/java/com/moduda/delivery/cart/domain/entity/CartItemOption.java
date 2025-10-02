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

@Table(name = "cart_item_options")
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemOption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "menu_id", nullable = false)
  Long menuId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cart_item_id", nullable = false)
  CartItem cartItem;

  @Column(name = "menu_option_value_id", nullable = false)
  Long menuOptionValueId;

  @Column(name = "menu_option_value_name", nullable = false)
  String menuOptionValueName;

  @Column(name = "option_price_snapshot", nullable = false)
  BigDecimal optionPriceSnapShot;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  LocalDateTime updatedAt;

  @Builder
  public CartItemOption(Long menuId, CartItem cartItem, Long menuOptionValueId,
      String menuOptionValueName, BigDecimal optionPriceSnapShot) {
    this.menuId = menuId;
    this.cartItem = cartItem;
    this.menuOptionValueId = menuOptionValueId;
    this.menuOptionValueName = menuOptionValueName;
    this.optionPriceSnapShot = optionPriceSnapShot;
  }
}
