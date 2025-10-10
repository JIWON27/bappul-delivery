package com.bappul.catalog.application.validator;

import static com.bappul.catalog.common.exception.ServiceExceptionCode.CATEGORY_HAS_STORES;
import static com.bappul.catalog.common.exception.ServiceExceptionCode.NOT_FOUND_CATEGORY;

import com.bappul.catalog.domain.entity.Category;
import com.bappul.catalog.domain.repository.CategoryRepository;
import com.bappul.catalog.domain.repository.StoreRepository;
import exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryValidator {

  private final CategoryRepository categoryRepository;
  private final StoreRepository storeRepository;

  public Category getCategoryById(Long categoryId) {
    return categoryRepository.findById(categoryId).orElseThrow(
        () -> new ServiceException(NOT_FOUND_CATEGORY)
    );
  }

  public void validateDeletable(Category category) {
    boolean exists = storeRepository.existsByCategory(category);
    if (exists) {
      throw new ServiceException(CATEGORY_HAS_STORES);
    }
  }
}
