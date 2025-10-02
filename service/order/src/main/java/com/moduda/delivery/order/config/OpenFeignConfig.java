package com.moduda.delivery.order.config;

import feign.Request.Options;
import feign.Retryer;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class OpenFeignConfig {

  @Bean
  public Options options(){
    return new Options(
        10000, TimeUnit.MILLISECONDS,
        5000, TimeUnit.MILLISECONDS,
        true
    );
  }

  @Bean
  public Retryer retryer(){
    return Retryer.NEVER_RETRY;
  }

  // 이게 뭐였지. 뭔가 OpenFeign 관련 JWT 인증인듯?
  @Bean
  public feign.RequestInterceptor feignRequestInterceptor(){
    return template -> {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth instanceof JwtAuthenticationToken token) {
        template.header("Authorization", "Bearer " + token.getToken().getTokenValue());
      }
    };
  }
}
