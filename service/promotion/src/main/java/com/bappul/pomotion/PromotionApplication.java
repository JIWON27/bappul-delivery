package com.bappul.pomotion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "com.bappul")
@EnableJpaRepositories(basePackages = "com.bappul")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.bappul"})
public class PromotionApplication {
  public static void main(String[] args) {
    SpringApplication.run(PromotionApplication.class, args);
  }
}
