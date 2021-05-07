package ch.valtech.kubernetes.microservice.cluster.security.config;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import java.util.Map;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

class UsernameSubClaimAdapterTest {

  private final UsernameSubClaimAdapter adapter = new UsernameSubClaimAdapter();

  @Test
  @SneakyThrows
  void testConvert() {
    String testToken = IOUtils.toString(getClass().getResourceAsStream("/test-token"), UTF_8);
    JWT jwt = JWTParser.parse(testToken);
    assertEquals(15, jwt.getJWTClaimsSet().getClaims().size());
    assertEquals("49a30b14-e057-464b-8f15-2dee517d9427", jwt.getJWTClaimsSet().getClaims().get("sub"));

    Map<String, Object> claimSet = adapter.convert(jwt.getJWTClaimsSet().getClaims());
    assertEquals(15, claimSet.size());
    assertEquals("vtc-keycloakadmin", claimSet.get("sub"));
  }

}
