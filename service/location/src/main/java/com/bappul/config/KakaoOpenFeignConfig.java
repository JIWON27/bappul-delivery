package com.bappul.config;

import feign.Request.Options;
import feign.Retryer;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KakaoOpenFeignConfig {

  @Value("${kakao.local.rest-api-key}")
  private String token;

  @Bean
  public Options options(){
    return new Options(
        10000, TimeUnit.MILLISECONDS,
        60000, TimeUnit.MILLISECONDS,
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
      template.header("Authorization", "KakaoAK " + token);
    };
  }
}
