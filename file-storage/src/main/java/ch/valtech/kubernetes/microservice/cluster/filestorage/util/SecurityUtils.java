package ch.valtech.kubernetes.microservice.cluster.filestorage.util;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtils {

  public static Optional<String> getJwt() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(securityContext.getAuthentication())
        .map(authentication -> {
          if (authentication instanceof JwtAuthenticationToken) {
            Jwt token = ((JwtAuthenticationToken) authentication).getToken();
            return token.getTokenValue();
          }
          return null;
        });
  }

  public static Optional<String> getUsername() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(securityContext.getAuthentication())
        .map(authentication -> {
          if (authentication instanceof JwtAuthenticationToken) {
            return authentication.getName();
          }
          return null;
        });
  }

}
