package com.bappul.delivery.payment.config;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PgConfig {

  @Value("pg.rest-api-key")
  private String apiKey;

  @Value("pg.rest-api-key-secret")
  private String apiKeySecret;

  @Bean
  public IamportClient importClient() {
    return new IamportClient(apiKey, apiKeySecret);
  }
}
