package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

import static net.devh.boot.grpc.common.security.SecurityConstants.AUTHORIZATION_HEADER;

import ch.valtech.kubernetes.microservice.cluster.filestorage.util.SecurityUtils;
import io.grpc.CallCredentials;
import io.grpc.Metadata;
import java.util.Optional;
import java.util.concurrent.Executor;
import net.devh.boot.grpc.client.inject.StubTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfiguration {

  @Bean
  public StubTransformer callCredentialsStubTransformer() {
    return (name, stub) -> stub.withCallCredentials(new JwtCallCredentials());
  }

  public static class JwtCallCredentials extends CallCredentials {

    @Override
    public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier) {
      Optional<String> jwtToken = SecurityUtils.getJwt();
      if (jwtToken.isPresent()) {
        Metadata extraHeaders = new Metadata();
        extraHeaders.put(AUTHORIZATION_HEADER, "Bearer " + jwtToken.get());
        applier.apply(extraHeaders);
      }
    }

    @Override
    public void thisUsesUnstableApi() {
      // API evolution in progress
    }

  }

}
