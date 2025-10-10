package com.bappul.delivery.catalog.web.v1.controller.store;

import com.bappul.delivery.catalog.application.service.StoreService;
import com.bappul.delivery.catalog.web.v1.request.store.StoreRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/stores")
public class StoreAdminController {

  private final StoreService storeService;

  @PostMapping
  public ResponseEntity<ApiResponse<Void>> enroll(@Valid @RequestBody StoreRequest request) {
    storeService.enroll(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success());
  }

  @DeleteMapping("/{storeId}")
  public ResponseEntity<Void> deleteById(@PathVariable Long storeId) {
    storeService.deleteById(storeId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

}
