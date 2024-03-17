package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.valtech.kubernetes.microservice.cluster.filestorage.mapper.AuditingMapper;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.AuditingRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.MessageResponse;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.ReactorPersistenceServiceGrpc.ReactorPersistenceServiceStub;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
class AuditingServiceGrpcTest {

  private final AuditingMapper auditingMapper = Mappers.getMapper(AuditingMapper.class);

  private final ReactorPersistenceServiceStub persistenceService = Mockito.mock(ReactorPersistenceServiceStub.class);

  private final AuditingService auditingService = new AuditingServiceGrpc(auditingMapper, persistenceService);

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(auditingService, "persistenceStub", persistenceService);
  }

  @Test
  void testSuccessfulAudit() {
    String message = "Test";
    when(persistenceService.audit(any(AuditingRequest.class))).thenReturn(Mono.just(MessageResponse.newBuilder()
        .setMessage(message)
        .build()));
    MessageDto response = auditingService.audit("test.txt", Action.UPLOAD);
    assertNotNull(response.getMessage());
    assertEquals(message, response.getMessage());
  }

  @Test
  void testSuccessfulAuditFailed() {
    when(persistenceService.audit(any(AuditingRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.PERMISSION_DENIED.withDescription("Permission denied")));
    assertThrows(ResponseStatusException.class, () -> auditingService.audit("test.txt", Action.UPLOAD));
  }

}
