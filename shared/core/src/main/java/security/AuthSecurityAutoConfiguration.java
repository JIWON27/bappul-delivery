package security;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@AutoConfiguration
public class AuthSecurityAutoConfiguration {

  @Bean
  Converter<Jwt, AbstractAuthenticationToken> jwtAuthConverter() {
    JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
    rolesConverter.setAuthoritiesClaimName("roles");
    rolesConverter.setAuthorityPrefix("ROLE_");

    JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
    jwtConverter.setJwtGrantedAuthoritiesConverter(rolesConverter);
    jwtConverter.setPrincipalClaimName("sub");

    return jwtConverter;
  }

  @Bean
  JwtDecoder jwtDecoder(@Value("${token.secret}") String secret) {
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    return NimbusJwtDecoder
        .withSecretKey(key)
        .macAlgorithm(MacAlgorithm.HS256)
        .build();
  }
}
