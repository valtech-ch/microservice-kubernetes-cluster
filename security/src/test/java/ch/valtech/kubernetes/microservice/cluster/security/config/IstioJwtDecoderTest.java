package ch.valtech.kubernetes.microservice.cluster.security.config;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;

class IstioJwtDecoderTest {

  private final IstioJwtDecoder decoder = new IstioJwtDecoder();

  @Test
  @SneakyThrows
  void testDecode() {
    String testToken = IOUtils.toString(getClass().getResourceAsStream("/test-token"), UTF_8);
    Jwt jwt = decoder.decode(testToken);

    Map<String, Object> headers = jwt.getHeaders();
    assertEquals(3, headers.size());
    assertEquals("WwmRGlIRXgV6O7-3tyhqf9aQUIMemackL93zPGs9_18", headers.get("kid"));
    assertEquals("JWT", headers.get("typ"));
    assertEquals("RS256", headers.get("alg"));

    Map<String, Object> claims = jwt.getClaims();
    assertEquals(15, claims.size());
    assertEquals("https://vtch-aks-demo.duckdns.org/auth/realms/master", claims.get("iss"));
    assertEquals("login-app", claims.get("azp"));
    assertEquals("Bearer", claims.get("typ"));
    assertEquals("vtc-keycloakadmin", claims.get("preferred_username"));
    assertEquals("vtc-keycloakadmin", claims.get("sub"));
  }

  @Test
  @SneakyThrows
  void testDecodeFailed() {
    Assertions.assertThrows(BadJwtException.class, () -> {
      decoder.decode("invalidToken");
    });
  }

}
