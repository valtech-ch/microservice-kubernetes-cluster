package ch.valtech.kubernetes.microservice.cluster.persistence.web.grpc;

import static net.devh.boot.grpc.client.security.CallCredentialsHelper.authorizationHeader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.valtech.kubernetes.microservice.cluster.persistence.AbstractIt;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.AuditingRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.AuditingRequest.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.MessageResponse;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.PersistenceServiceGrpc.PersistenceServiceBlockingStub;
import io.grpc.StatusRuntimeException;
import lombok.SneakyThrows;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersistenceControllerGrpcIt extends AbstractIt {


  @GrpcClient("inProcess")
  private PersistenceServiceBlockingStub persistenceStub;

  @Test
  @SneakyThrows
  void shouldPostNewMessage() {
    String testToken = IOUtils.toString(getClass().getResourceAsStream("/test-token"));

    AuditingRequest request = AuditingRequest.newBuilder()
        .setAction(Action.UPLOAD)
        .setFilename("some file")
        .build();
    MessageResponse response = persistenceStub
        .withCallCredentials(authorizationHeader("Bearer " + testToken))
        .audit(request);
    assertNotNull(response);
    assertEquals("User vtc-keycloakadmin uploaded some file", response.getMessage());
  }

  @Test
  void shouldFailPostNewMessage() {
    AuditingRequest request = AuditingRequest.newBuilder()
        .setAction(Action.UPLOAD)
        .setFilename("some file")
        .build();
    assertThrows(StatusRuntimeException.class, () -> {
      persistenceStub.audit(request);
    });
  }

}
