package ch.valtech.kubernetes.microservice.cluster.persistence.util;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtils {

  public static final String KEYCLOAK_USERNAME = "preferred_username";

  public static Optional<String> getUsername() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(securityContext.getAuthentication())
        .map(authentication -> {
          if (authentication instanceof JwtAuthenticationToken) {
            return authentication.getName();
          } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            return springSecurityUser.getUsername();
          }
          return null;
        });
  }

  @SneakyThrows
  public static Jwt decode(String token) throws JwtException {
    JWT jwt = JWTParser.parse(token);
    return createJwt(token, jwt);
  }

  private static Jwt createJwt(String token, JWT parsedJwt) {
    try {
      Map<String, Object> headers = new LinkedHashMap<>(parsedJwt.getHeader().toJSONObject());
      Map<String, Object> claims = parsedJwt.getJWTClaimsSet().getClaims();
      String username = claims.get(KEYCLOAK_USERNAME) != null ? claims.get(KEYCLOAK_USERNAME).toString()
          : claims.get(JwtClaimNames.SUB).toString();
      return Jwt.withTokenValue(token)
          .headers(h -> h.putAll(headers))
          .claims(c -> c.putAll(convertDates(claims)))
          .subject(username)
          .build();
    } catch (Exception ex) {
      if (ex.getCause() instanceof ParseException) {
        throw new JwtException(String.format("Could not decode payload. Reson: %s", "Malformed payload"));
      } else {
        throw new JwtException(String.format("Decoding threw message %s", ex.getMessage()), ex);
      }
    }
  }

  private static Map<String, Object> convertDates(Map<String, Object> claims) {
    Map<String, Object> modifiedClaims = new HashMap<>(claims);
    if (modifiedClaims.get(JwtClaimNames.IAT) != null) {
      Date date = (Date) modifiedClaims.get(JwtClaimNames.IAT);
      modifiedClaims.put(JwtClaimNames.IAT, date.toInstant());
    }
    if (modifiedClaims.get(JwtClaimNames.EXP) != null) {
      Date date = (Date) modifiedClaims.get(JwtClaimNames.EXP);
      modifiedClaims.put(JwtClaimNames.EXP, date.toInstant());
    }
    if (modifiedClaims.get(JwtClaimNames.NBF) != null) {
      Date date = (Date) modifiedClaims.get(JwtClaimNames.NBF);
      modifiedClaims.put(JwtClaimNames.NBF, date.toInstant());
    }
    return modifiedClaims;
  }

}
