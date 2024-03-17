package ch.valtech.kubernetes.microservice.cluster.persistence.web.grpc;

import static ch.valtech.kubernetes.microservice.cluster.persistence.util.TestSecurityUtils.authorizationHeader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.valtech.kubernetes.microservice.cluster.persistence.AbstractIt;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.AuditingRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.AuditingRequest.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.MessageResponse;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.PersistenceServiceGrpc;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.PersistenceServiceGrpc.PersistenceServiceBlockingStub;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.ReactorPersistenceServiceGrpc;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.SearchRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.AuditingRepository;
import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.Iterator;
import java.util.List;
import lombok.SneakyThrows;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersistenceControllerGrpcIt extends AbstractIt {

  @GrpcClient("inProcess")
  private PersistenceServiceBlockingStub persistenceStub;

  @Autowired
  private AuditingRepository repository;

  @AfterEach
  void tearDown() {
    repository.deleteAll().block();
  }

  @Test
  @SneakyThrows
  void shouldPostNewMessage() {
    String testToken = IOUtils.toString(getClass().getResourceAsStream("/test-token"));

    MessageResponse response = createMessage(testToken);
    assertNotNull(response);
    assertEquals("User vtc-keycloakadmin uploaded some-file.txt", response.getMessage());
  }

  @Test
  void shouldFailPostNewMessage() {
    AuditingRequest request = AuditingRequest.newBuilder()
        .setAction(Action.UPLOAD)
        .setFilename("some-file.txt")
        .build();
    assertThrows(StatusRuntimeException.class, () -> {
      persistenceStub.audit(request);
    });
  }

  @Test
  @SneakyThrows
  void shouldGetMessages() {
    String testToken = IOUtils.toString(getClass().getResourceAsStream("/test-token"));

    createMessage(testToken);

    SearchRequest request = SearchRequest.newBuilder()
        .setFilename("some-file.txt")
        .setLimit(2)
        .build();

    List<MessageResponse> list = IteratorUtils.toList(persistenceStub
        .withCallCredentials(authorizationHeader(testToken))
        .search(request));

    assertEquals(1, list.size());
    assertEquals("User vtc-keycloakadmin uploaded some-file.txt", list.get(0).getMessage());
  }

  @Test
  void shouldFailGetMessages() {
    SearchRequest request = SearchRequest.newBuilder()
        .setFilename("test.txt")
        .setLimit(2)
        .build();
    Iterator<MessageResponse> responseIterator = persistenceStub.search(request);
    assertThrows(StatusRuntimeException.class, () -> {
      responseIterator.next();
    });
  }

  private MessageResponse createMessage(String testToken) {
    AuditingRequest request = AuditingRequest.newBuilder()
        .setAction(Action.UPLOAD)
        .setFilename("some-file.txt")
        .build();

    return persistenceStub
        .withCallCredentials(authorizationHeader(testToken))
        .audit(request);
  }

}
