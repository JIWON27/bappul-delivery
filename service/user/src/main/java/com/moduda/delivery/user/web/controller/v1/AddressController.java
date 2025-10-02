package com.moduda.delivery.user.web.controller.v1;

import com.moduda.delivery.user.application.service.AddressService;
import com.moduda.delivery.user.web.dto.AddressRequest;
import com.moduda.delivery.user.web.dto.AddressResponse;
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

  /**
   * 속도 조절을 위해서 Address는 최소한의 기능만 구현하였습니다.
   * 나머지 기능들은 집중하고 싶은 부분 기능 구현이 마무리된 후 구현하겠습니다.
   */

  private final AddressService addressService;

  @PostMapping
  public ResponseEntity<ApiResponse<AddressResponse>> enroll(
      @Valid @RequestBody AddressRequest addressRequest,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId) {
    AddressResponse response = addressService.registerAddress(addressRequest, Long.valueOf(userId));
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<AddressResponse>> getAddress(@AuthenticationPrincipal(expression = "claims['uid']") String userId) {
    AddressResponse response = addressService.getAddress(Long.valueOf(userId));
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
  }

}
