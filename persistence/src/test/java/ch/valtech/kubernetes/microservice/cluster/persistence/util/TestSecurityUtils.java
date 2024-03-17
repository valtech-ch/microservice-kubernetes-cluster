package ch.valtech.kubernetes.microservice.cluster.persistence.util;

import io.grpc.CallCredentials;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestSecurityUtils {

  public static CallCredentials authorizationHeader(String token) {
    return CallCredentialsHelper.bearerAuth(() -> token);
  }

}
