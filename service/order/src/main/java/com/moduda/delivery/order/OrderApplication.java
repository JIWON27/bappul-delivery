package com.moduda.delivery.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class OrderApplication {
  public static void main(String[] args) {
    SpringApplication.run(OrderApplication.class, args);
  }
}
