package com.bappul.delivery.user.web.dto;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
  Long id;
  UUID uuid;
  String name;
  String email;
  String nickname;
  String phone;
  String imageUrl;
}
