package com.bappul.delivery.catalog.application.mapper;

import com.bappul.delivery.catalog.domain.entity.Category;
import com.bappul.delivery.catalog.web.v1.request.category.CategoryRequest;
import com.bappul.delivery.catalog.web.v1.response.category.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  Category toCategory(CategoryRequest request);

  @Mapping(target = "name", source = "category.name")
  CategoryResponse toResponse(Category category);
}
