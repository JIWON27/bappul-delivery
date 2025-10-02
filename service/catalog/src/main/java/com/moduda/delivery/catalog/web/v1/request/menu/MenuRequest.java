package com.moduda.delivery.catalog.web.v1.request.menu;

import com.moduda.delivery.catalog.domain.entity.Menu;
import com.moduda.delivery.catalog.domain.entity.Store;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuRequest {
  String name;
  BigDecimal price;
  String description;

  public Menu toEntity(Store store, String photoUrl){
    return Menu.builder()
        .store(store)
        .name(name)
        .description(description)
        .photoUrl(photoUrl)
        .price(price)
        .sortOrder(0)// 임시값
        .build();
  }
}
