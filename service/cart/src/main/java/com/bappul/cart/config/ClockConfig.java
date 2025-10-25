package com.bappul.cart.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {

  @Bean
  public Clock koreaClock() {
    return Clock.system(ZoneId.of("Asia/Seoul"));
  }
}
