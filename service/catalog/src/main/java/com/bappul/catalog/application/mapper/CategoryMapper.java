package com.bappul.catalog.application.mapper;

import com.bappul.catalog.domain.entity.Category;
import com.bappul.catalog.web.v1.request.category.CategoryRequest;
import com.bappul.catalog.web.v1.response.category.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  Category toCategory(CategoryRequest request);

  @Mapping(target = "name", source = "category.name")
  CategoryResponse toResponse(Category category);
}
