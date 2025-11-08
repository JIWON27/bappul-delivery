package com.bappul.delivery.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.bappul")
@EnableJpaRepositories(basePackages = "com.bappul")
public class JpaConfig {

}
