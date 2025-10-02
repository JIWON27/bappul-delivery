package com.bappul.delivery.catalog.web.v1.controller.category;

import com.bappul.delivery.catalog.application.service.CategoryService;
import com.bappul.delivery.catalog.web.v1.request.category.CategoryRequest;
import com.bappul.delivery.catalog.web.v1.response.category.CategoryResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/category")
public class CategoryAdminController {

  /**
   * 카테고리 관리자 API
   * [POST] 카테고리 등록 API
   * [GET] 카테고리 전체 조회 API
   * [DELETE] 카테고리 삭제 API
   */

  private final CategoryService categoryService;

  @PostMapping
  public ResponseEntity<ApiResponse<Void>> create(@RequestBody @Valid CategoryRequest request) {
    categoryService.create(request);
    return ResponseEntity.status(200).body(ApiResponse.success());
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
    List<CategoryResponse> categories = categoryService.getCategories();
    return ResponseEntity.status(200).body(ApiResponse.success(categories));
  }

  @DeleteMapping("/{categoryId}")
  public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable Long categoryId) {
    categoryService.deleteById(categoryId);
    return ResponseEntity.status(200).body(ApiResponse.success());
  }
}
