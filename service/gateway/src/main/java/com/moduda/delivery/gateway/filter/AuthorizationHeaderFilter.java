package com.moduda.delivery.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

  private final JwtParser jwtParser;

  public AuthorizationHeaderFilter(@Value("${token.secret}") String secret) {
    super(Config.class);

    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
    this.jwtParser = Jwts.parser()
        .clockSkewSeconds(60)
        .verifyWith(secretKey)
        .build();
  }
  
  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();
      String authorizationHeader = request.getHeaders().getFirst("Authorization");
      if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
        return chain.filter(exchange);
      }

      if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        return unauthorized(exchange, "JWT 토큰 누락");
      }

      String jwt = authorizationHeader.substring(7);

      if (!isJwtValid(jwt)) {
        return unauthorized(exchange, "JWT 인증 실패");
      }

      return chain.filter(exchange);
    };
  }

  private boolean isJwtValid(String jwt){
    try {
      Jws<Claims> jws = jwtParser.parseSignedClaims(jwt);
      Claims claims = jws.getPayload();

      Date expiration = claims.getExpiration();
      if (expiration == null || expiration.before(new Date())) {
        return false;
      }
      String subject = claims.getSubject();
      if (subject == null || subject.isEmpty()) {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public static class Config {}

  private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    byte[] bytes = ("{\"error\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
  }
}
