package com.moduda.delivery.catalog.application.mapper;

import com.moduda.delivery.catalog.domain.entity.Category;
import com.moduda.delivery.catalog.web.v1.response.category.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  @Mapping(target = "name", source = "category.name")
  CategoryResponse toResponse(Category category);
}
