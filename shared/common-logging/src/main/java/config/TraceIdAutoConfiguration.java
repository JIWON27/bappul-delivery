package config;

import filter.TraceIdFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(name = {
    "jakarta.servlet.Filter",
    "org.springframework.web.filter.OncePerRequestFilter"
})
public class TraceIdAutoConfiguration {

  @Bean
  public TraceIdFilter traceIdFilter() {
    return new TraceIdFilter();
  }

}
