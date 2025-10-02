package com.moduda.delivery.user.common.jwt.filter;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "token")
public class TokenProperties {
  private final long expiration_time;
  private final String secret;

  public TokenProperties(long expiration_time, String secret) {
    this.expiration_time = expiration_time;
    this.secret = secret;
  }
}
