package com.moduda.delivery.catalog.web.v1.response.search;

import com.moduda.delivery.catalog.domain.entity.Store;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreSearchResponse {
  Long storeId;
  String storeName;
  String categoryName;
  BigDecimal minOrderPrice;
  BigDecimal deliveryFee;

  public static StoreSearchResponse from(Store store){
    return StoreSearchResponse.builder()
        .storeId(store.getId())
        .storeName(store.getName())
        .categoryName(store.getCategory().getName())
        .minOrderPrice(store.getMinOrderPrice())
        .deliveryFee(store.getDeliveryFee())
        .build();
  }
}
