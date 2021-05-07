package ch.valtech.kubernetes.microservice.cluster.security.config;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.vavr.collection.Stream;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakRealmRoleConverterTest {

  private final KeycloakRealmRoleConverter converter = new KeycloakRealmRoleConverter();

  private final IstioJwtDecoder decoder = new IstioJwtDecoder();

  @Test
  @SneakyThrows
  void testConvert() {
    String testToken = IOUtils.toString(getClass().getResourceAsStream("/test-token"), UTF_8);
    Jwt jwt = decoder.decode(testToken);
    List<String> authorities = Stream.ofAll(converter.convert(jwt))
        .map(GrantedAuthority::getAuthority)
        .asJava();
    assertEquals(4, authorities.size());
    assertTrue(authorities.contains("ROLE_create-realm"));
    assertTrue(authorities.contains("ROLE_offline_access"));
    assertTrue(authorities.contains("ROLE_admin"));
    assertTrue(authorities.contains("ROLE_uma_authorization"));
  }

}
