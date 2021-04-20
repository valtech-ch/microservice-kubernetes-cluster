package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IstioJwtDecoder implements JwtDecoder {

  @Override
  public Jwt decode(String token) throws JwtException {
    try {
      JWT jwt = JWTParser.parse(token);
      return createJwt(token, jwt);
    } catch (Exception ex) {
      log.trace("Failed to parse token", ex);
      throw new BadJwtException(
          String.format("An error occurred while attempting to decode the Jwt: %s", ex.getMessage()), ex);
    }
  }

  private static Jwt createJwt(String token, JWT parsedJwt) {
    try {
      JWTClaimsSet jwtClaimsSet = parsedJwt.getJWTClaimsSet();
      Map<String, Object> headers = new LinkedHashMap<>(parsedJwt.getHeader().toJSONObject());
      Map<String, Object> claims = new UsernameSubClaimAdapter().convert(jwtClaimsSet.getClaims());
      // @formatter:off
      return Jwt.withTokenValue(token)
          .headers((h) -> h.putAll(headers))
          .claims((c) -> c.putAll(claims))
          .build();
    } catch (Exception ex) {
      log.trace("Failed to process JWT", ex);
      if (ex.getCause() instanceof ParseException) {
        throw new BadJwtException(String.format("Could not decode payload. Reson: %s", "Malformed payload"));
      } else {
        throw new BadJwtException(String.format("Decoding threw message %s", ex.getMessage()), ex);
      }
    }
  }

}
