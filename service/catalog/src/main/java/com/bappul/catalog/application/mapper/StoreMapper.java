package com.bappul.catalog.application.mapper;

import com.bappul.catalog.domain.entity.Category;
import com.bappul.catalog.domain.entity.Store;
import com.bappul.catalog.web.v1.request.store.StoreRequest;
import com.bappul.catalog.web.v1.response.store.StoreResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StoreMapper {

  @Mapping(target = "name", source = "request.name")
  @Mapping(target = "category", source = "category")
  @Mapping(target = "userId", source = "request.ownerId")
  Store toStore(StoreRequest request, Category category);

  @Mapping(target = "category", source = "store.category.name")
  StoreResponse toResponse(Store store);

}
