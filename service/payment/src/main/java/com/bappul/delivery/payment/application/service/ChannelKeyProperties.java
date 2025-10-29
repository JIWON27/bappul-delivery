package com.bappul.delivery.payment.application.service;

import com.bappul.delivery.payment.domain.entity.PgProvider;
import java.util.HashMap;
import java.util.Map;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@ConfigurationProperties(prefix = "payments")
public class ChannelKeyProperties {
  private Map<PgProvider, String> channels = new HashMap<>();

  public Map<PgProvider, String> getChannels() {
    return channels;
  }

}
