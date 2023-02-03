package ch.valtech.kubernetes.microservice.cluster.persistence.util;

import io.grpc.CallCredentials;
import java.nio.ByteBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.lognet.springboot.grpc.security.AuthCallCredentials;
import org.lognet.springboot.grpc.security.AuthHeader;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestSecurityUtils {

  public static CallCredentials authorizationHeader(String token) {
    return new AuthCallCredentials(
        AuthHeader.builder()
            .bearer()
            .binaryFormat(true)
            .tokenSupplier(() -> ByteBuffer.wrap(token.getBytes()))
    );
  }

}
