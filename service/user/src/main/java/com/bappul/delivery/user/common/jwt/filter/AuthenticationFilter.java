package com.bappul.delivery.user.common.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bappul.delivery.user.common.jwt.CustomUserDetails;
import com.bappul.delivery.user.web.dto.LoginRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final TokenProperties tokenProperties;
  private final ObjectMapper objectMapper;
  private final List<String> audienceService = List.of("catalog", "promotion", "order", "payment", "delivery");

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    try {
      LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
      return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
    } catch (Exception e) {
      throw new AuthenticationServiceException("로그인 인증 실패", e);
    }
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain, 
      Authentication authResult) throws IOException, ServletException {

    CustomUserDetails principal = (CustomUserDetails) authResult.getPrincipal();
    String jwt = generateJsonWebToken(principal);

    long expiresIn = tokenProperties.getExpiration_time();

    response.setContentType("application/json");
    TokenResponse tokenResponse = TokenResponse.of(jwt, "Bearer", expiresIn, null, null);
    String body = objectMapper.writeValueAsString(tokenResponse);
    response.getWriter().write(body);
    response.getWriter().flush();
  }

  private String generateJsonWebToken(CustomUserDetails principal) {
    byte[] keyBytes = tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8);
    SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
    String id = principal.getUserId().toString();
    String uuid = principal.getUuid().toString();

    List<String> roles = principal.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
        .collect(Collectors.toList());

    Instant now = Instant.now();

    return Jwts.builder()
        .issuer("moduda")
        .audience().add(audienceService).and()
        .subject(uuid)
        .claim("uid", id)
        .claim("roles", roles)
        .expiration(Date.from(now.plusMillis(tokenProperties.getExpiration_time())))
        .issuedAt(Date.from(now))
        .notBefore(Date.from(now))
        .signWith(secretKey, Jwts.SIG.HS256)
        .compact();
  }
}
