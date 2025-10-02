package com.moduda.delivery.catalog.application.service;

import com.moduda.delivery.catalog.application.mapper.CategoryMapper;
import com.moduda.delivery.catalog.application.validator.CategoryValidator;
import com.moduda.delivery.catalog.domain.entity.Category;
import com.moduda.delivery.catalog.domain.repository.CategoryRepository;
import com.moduda.delivery.catalog.web.v1.request.category.CategoryRequest;
import com.moduda.delivery.catalog.web.v1.response.category.CategoryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryValidator categoryValidator;
  private final CategoryMapper categoryMapper;

  @Transactional
  public void create(CategoryRequest request) {
    Category category = request.toEntity();
    categoryRepository.save(category);
  }

  @Transactional(readOnly = true)
  public List<CategoryResponse> getCategories(){
    List<Category> categories = categoryRepository.findAll();
    return  categories.stream()
        .map(categoryMapper::toResponse)
        .toList();
  }

  @Transactional
  public void deleteById(Long categoryId) {
    Category category = categoryValidator.getCategoryById(categoryId);
    categoryValidator.validateDeletable(category);

    categoryRepository.deleteById(categoryId);
  }
}
