package com.moduda.delivery.user.web.dto;

import com.moduda.delivery.user.domain.entity.user.Gender;
import com.moduda.delivery.user.domain.entity.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {

  @NotBlank(message = "이메일은 필수입니다.")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  String email;

  @NotBlank(message = "비밀번호는 필수입니다.")
  @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
  String password;

  @Pattern(regexp = "^010\\d{8}$", message = "휴대폰 번호는 010으로 시작하는 11자리 숫자여야 합니다.")
  String phone;

  @NotBlank(message = "닉네임은 필수입니다.")
  @Size(max = 20, message = "닉네임은 최대 20자까지 가능합니다.")
  String nickname;

  Gender gender;

  @NotNull(message = "생년월일은 필수입니다.")
  LocalDate birthDate;

  public User toEntity(UUID uuid) {
    return User.builder()
        .uuid(uuid)
        .email(email)
        .nickname(nickname)
        .phone(phone)
        .birthDate(birthDate)
        .gender(gender)
        .isVerified(false)
        .isActive(true)
        .build();
  }
}
