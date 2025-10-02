package com.moduda.delivery.user.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moduda.delivery.user.common.jwt.filter.AuthenticationFilter;
import com.moduda.delivery.user.common.jwt.filter.TokenProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private static final String[] SECURITY_EXCLUDE_PATHS = {
      "/public/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**",
      "/favicon.ico", "/actuator/**", "/swagger-resources/**",
      "/external/**"
  };

  private final Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationFilter authenticationFilter,
      Converter<Jwt, AbstractAuthenticationToken> jwtAuthConverter) throws Exception {
    http
        .cors(withDefaults()) // 기본적인 cors 에러 관련 정책 생성
        .csrf(AbstractHttpConfigurer::disable) // 토큰 탈취 예방
        .formLogin(AbstractHttpConfigurer::disable) // 기본 UsernamePasswordAuthenticationFilter 비활성화
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(SECURITY_EXCLUDE_PATHS).permitAll()
            .requestMatchers("/api/v1/login").permitAll()
            .requestMatchers("/api/v1/users").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth ->
            oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
        .addFilterAt(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
    return configuration.getAuthenticationManager();
  }

  @Bean
  public AuthenticationFilter getAuthenticationFiler(AuthenticationManager authenticationManager, TokenProperties tokenProperties, ObjectMapper objectMapper) throws Exception {
    AuthenticationFilter authenticationFilter = new AuthenticationFilter(tokenProperties, objectMapper);
    authenticationFilter.setAuthenticationManager(authenticationManager);
    authenticationFilter.setFilterProcessesUrl("/api/v1/login");
    return authenticationFilter;
  }

  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }
}

