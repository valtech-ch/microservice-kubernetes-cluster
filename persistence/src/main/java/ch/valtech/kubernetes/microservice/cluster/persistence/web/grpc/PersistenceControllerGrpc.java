package ch.valtech.kubernetes.microservice.cluster.persistence.web.grpc;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.AuditingRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.MessageResponse;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.PersistenceServiceGrpc;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.access.prepost.PreAuthorize;

@Slf4j
@GrpcService
public class PersistenceControllerGrpc extends PersistenceServiceGrpc.PersistenceServiceImplBase {

  private final PersistenceService persistenceService;

  public PersistenceControllerGrpc(PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
  }

  @Override
  @PreAuthorize("hasAnyRole('admin', 'user')")
  public void audit(AuditingRequest request, StreamObserver<MessageResponse> responseObserver) {
    log.info("gRPC Received!");
    MessageDto newMessage = persistenceService.saveNewMessage(AuditingRequestDto.builder()
        .filename(request.getFilename())
        .action(Action.valueOf(request.getAction().toString()))
        .build());

    MessageResponse response = MessageResponse.newBuilder()
        .setMessage(newMessage.getMessage())
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

}
