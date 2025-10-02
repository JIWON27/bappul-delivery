package com.moduda.delivery.user.web.controller.v1;

import com.moduda.delivery.user.application.service.UserService;
import com.moduda.delivery.user.web.dto.UserRequest;
import com.moduda.delivery.user.web.dto.UserResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<ApiResponse<UserResponse>> join(@Valid @RequestBody UserRequest request) {
    UserResponse userResponse = userService.join(request);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(userResponse));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
    UserResponse userResponse = userService.getUserById(userId);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(userResponse));
  }

  @GetMapping("/uuid/{uuid}")
  public ResponseEntity<ApiResponse<UserResponse>> getUserByUuid(@PathVariable UUID uuid) {
    UserResponse userResponse = userService.getUserByUuid(uuid);
    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(userResponse));
  }

  /**
   * TODO 프로필 설정 API
   * TODO 권한 부여 API
   */
}
