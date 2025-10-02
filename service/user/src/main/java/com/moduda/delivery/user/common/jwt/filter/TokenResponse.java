package com.moduda.delivery.user.common.jwt.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenResponse {
  String accessToken;
  String type;
  long expiresIn;
  String refreshToken;
  String scope;

  public static TokenResponse of(String accessToken, String type, long expiresIn, String refreshToken, String scope) {
    return new TokenResponse(accessToken, type, expiresIn, refreshToken, scope);
  }
}
