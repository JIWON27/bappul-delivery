package com.bappul.catalog.application.mapper;

import com.bappul.catalog.adapter.response.LocationResponse;
import com.bappul.catalog.domain.entity.Category;
import com.bappul.catalog.domain.entity.Store;
import com.bappul.catalog.web.v1.request.store.StoreRequest;
import com.bappul.catalog.web.v1.response.store.StoreLocationResponse;
import com.bappul.catalog.web.v1.response.store.StoreResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StoreMapper {

  @Mapping(target = "name", source = "request.name")
  @Mapping(target = "category", source = "category")
  @Mapping(target = "jibunAddress", source = "location.jibunAddress")
  @Mapping(target = "haengjeongCode", source = "location.haengJeongCode")
  @Mapping(target = "beopjeongCode", source = "location.beopJeongCode")
  @Mapping(target = "zipcode", source = "location.zoneNo")
  @Mapping(target = "roadAddress", source = "location.roadAddress")
  @Mapping(target = "longitude", source = "location.longitude")
  @Mapping(target = "latitude", source = "location.latitude")
  Store toStore(StoreRequest request, Category category, LocationResponse location);

  @Mapping(target = "category", source = "store.category.name")
  @Mapping(target = "storeName", source = "store.name")
  @Mapping(target = "storeId", source = "store.id")
  StoreResponse toResponse(Store store);

  StoreLocationResponse toStoreLocationResponse(Store store);

}
