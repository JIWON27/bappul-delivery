package com.bappul.catalog.config;

import feign.Request.Options;
import feign.Retryer;
import java.util.concurrent.TimeUnit;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;

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

  @Bean
  public feign.RequestInterceptor feignRequestInterceptor(){
    return template -> {
      String traceId = MDC.get("traceId");
      if (StringUtils.hasText(traceId) && !template.headers().containsKey("X-Request-Id")) {
        template.header("X-Request-Id", traceId);
      }
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth instanceof JwtAuthenticationToken token) {
        template.header("Authorization", "Bearer " + token.getToken().getTokenValue());
      }
    };
  }
}
