package com.bappul.image;

import com.bappul.image.service.ImageService;
import com.bappul.image.service.LocalImageService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ImageCoreAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(ImageService.class)
  public ImageService imageService() {
    return new LocalImageService();
  }

}
