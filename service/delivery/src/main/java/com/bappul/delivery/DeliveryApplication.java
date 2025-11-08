package com.bappul.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.bappul"})
public class DeliveryApplication {
  public static void main(String[] args) {
    SpringApplication.run(DeliveryApplication.class, args);
  }
}
