package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.MessageResponse;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.PersistenceServiceGrpc.PersistenceServiceBlockingStub;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
class AuditingServiceGrpcTest {

  private final AuditingService auditingService = new AuditingServiceGrpc();

  private final PersistenceServiceBlockingStub persistenceService = Mockito.mock(PersistenceServiceBlockingStub.class);

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(auditingService, "persistenceStub", persistenceService);
  }

  @Test
  void testSuccessfulAudit() {
    String message = "Test";
    when(persistenceService.audit(any())).thenReturn(MessageResponse.newBuilder()
        .setMessage(message)
        .build());
    MessageDto response = auditingService.audit("test.txt", Action.UPLOAD);
    assertNotNull(response.getMessage());
    assertEquals(message, response.getMessage());
  }

  @Test
  void testSuccessfulAuditFailed() {
    when(persistenceService.audit(any()))
        .thenThrow(new StatusRuntimeException(Status.PERMISSION_DENIED.withDescription("Permission denied")));
    MessageDto response = auditingService.audit("test.txt", Action.UPLOAD);
    assertNotNull(response.getMessage());
    assertEquals("Permission denied", response.getMessage());
  }

}
