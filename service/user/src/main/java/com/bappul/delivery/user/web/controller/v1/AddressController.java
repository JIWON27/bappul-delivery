package com.bappul.delivery.user.web.controller.v1;

import com.bappul.delivery.user.application.service.AddressService;
import com.bappul.delivery.user.web.dto.AddressRequest;
import com.bappul.delivery.user.web.dto.AddressResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/address")
public class AddressController {

  private final AddressService addressService;

  @PostMapping
  public ResponseEntity<ApiResponse<AddressResponse>> enrollAddress(
      @Valid @RequestBody AddressRequest addressRequest,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId) {
    AddressResponse response = addressService.enrollAddress(addressRequest, Long.valueOf(userId));
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<AddressResponse>> getAddress(@AuthenticationPrincipal(expression = "claims['uid']") String userId) {
    AddressResponse response = addressService.getAddress(Long.valueOf(userId));
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
  }

}
