package com.bappul.delivery.catalog.web.v1.request.category;

import com.bappul.delivery.catalog.domain.entity.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequest {
  String name;

  public Category toEntity() {
    return new Category(name);
  }
}
