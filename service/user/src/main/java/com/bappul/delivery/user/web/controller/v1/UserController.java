package com.bappul.delivery.user.web.controller.v1;

import com.bappul.delivery.user.application.service.UserService;
import com.bappul.delivery.user.web.dto.UserRequest;
import com.bappul.delivery.user.web.dto.UserResponse;
import com.bappul.image.service.ImageDownload;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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

  @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> updateProfile(
      @RequestParam("profile") MultipartFile file,
      @AuthenticationPrincipal(expression = "claims['uid']") String userId)
  {
    userService.updateProfile(file, Long.valueOf(userId));
    return  ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("/{userId}/profile/download")
  public ResponseEntity<Resource> downloadProfile(@PathVariable Long userId) {
    ImageDownload imageDownload = userService.downloadProfile(userId);

    HttpHeaders headers = new HttpHeaders();
    headers.setETag(imageDownload.getETag());
    headers.setContentType(MediaType.valueOf(imageDownload.getContentType()));
    headers.setContentLength(imageDownload.getContentLength());
    headers.setContentDisposition(ContentDisposition.inline()
        .filename(imageDownload.getFileName(), StandardCharsets.UTF_8)
        .build());

    return new ResponseEntity<>(imageDownload.getResource(), headers, HttpStatus.OK);
  }
}
