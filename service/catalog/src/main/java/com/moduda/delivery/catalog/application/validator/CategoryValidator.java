package com.moduda.delivery.catalog.application.validator;

import static com.moduda.delivery.catalog.common.exception.ServiceExceptionCode.CATEGORY_HAS_STORES;
import static com.moduda.delivery.catalog.common.exception.ServiceExceptionCode.NOT_FOUND_CATEGORY;

import com.moduda.delivery.catalog.domain.entity.Category;
import com.moduda.delivery.catalog.domain.repository.CategoryRepository;
import com.moduda.delivery.catalog.domain.repository.StoreRepository;
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
