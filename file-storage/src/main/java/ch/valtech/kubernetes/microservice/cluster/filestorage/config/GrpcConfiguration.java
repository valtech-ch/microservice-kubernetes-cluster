package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

import ch.valtech.kubernetes.microservice.cluster.filestorage.util.SecurityUtils;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;
import org.lognet.springboot.grpc.security.AuthClientInterceptor;
import org.lognet.springboot.grpc.security.AuthHeader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfiguration {

  @Bean
  public AuthClientInterceptor grpcAuthClientInterceptor() {
    return new AuthClientInterceptor(
        AuthHeader.builder()
            .bearer()
            .binaryFormat(true)
            .tokenSupplier(this::generateToken)
    );
  }

  private ByteBuffer generateToken() {
    Optional<String> jwtToken = SecurityUtils.getJwt();
    if (jwtToken.isPresent()) {
      return ByteBuffer.wrap(jwtToken.get().getBytes());
    }

    return ByteBuffer.wrap(UUID.randomUUID().toString().getBytes());
  }

}
