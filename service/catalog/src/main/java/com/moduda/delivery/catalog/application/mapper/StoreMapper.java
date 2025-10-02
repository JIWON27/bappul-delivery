package com.moduda.delivery.catalog.application.mapper;

import com.moduda.delivery.catalog.domain.entity.Category;
import com.moduda.delivery.catalog.domain.entity.Store;
import com.moduda.delivery.catalog.web.v1.request.store.StoreRequest;
import com.moduda.delivery.catalog.web.v1.response.store.StoreResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StoreMapper {

  @Mapping(target = "name", source = "request.name")
  @Mapping(target = "category", source = "category")
  Store toStore(StoreRequest request, Category category, Long userId);
  @Mapping(target = "category", source = "store.category.name")
  StoreResponse toResponse(Store store);

}
