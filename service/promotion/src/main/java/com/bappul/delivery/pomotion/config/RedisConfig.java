package com.bappul.delivery.pomotion.config;

/*
@Configuration
public class RedisConfig {

  private final String host;
  private final int port;

  public RedisConfig(
      @Value("${spring.data.redis.host}") String host,
      @Value("${spring.data.redis.port}") int port) {
    this.host = host;
    this.port = port;
  }

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(host, port);
  }

  @Bean
  public RedisTemplate<String, Long> redisTemplate() {
    RedisTemplate<String, Long> template = new RedisTemplate<>();

    template.setConnectionFactory(redisConnectionFactory());

    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericToStringSerializer<>(Long.class));

    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));

    return template;
  }

}
*/
