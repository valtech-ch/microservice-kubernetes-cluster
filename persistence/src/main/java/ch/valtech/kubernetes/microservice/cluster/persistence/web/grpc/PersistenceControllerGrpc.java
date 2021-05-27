package ch.valtech.kubernetes.microservice.cluster.persistence.web.grpc;

import static ch.valtech.kubernetes.microservice.cluster.persistence.util.SecurityUtils.getUsername;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.AuditingRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.MessageResponse;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.PersistenceServiceGrpc;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.SearchRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class PersistenceControllerGrpc extends PersistenceServiceGrpc.PersistenceServiceImplBase {

  private final PersistenceService persistenceService;

  public PersistenceControllerGrpc(PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
  }

  @Override
  @PreAuthorize("hasAnyRole('admin', 'user')")
  public void audit(AuditingRequest request, StreamObserver<MessageResponse> responseObserver) {
    log.info("gRPC Received!");
    String username = getUsername().orElseThrow(() ->
        new ResponseStatusException(FORBIDDEN, "Username not found"));
    MessageDto newMessage = persistenceService
        .saveNewMessage(ReactivePersistenceControllerGrpc.toAuditingRequestDto(request), username)
        .block();

    responseObserver.onNext(ReactivePersistenceControllerGrpc.toMessageRespone(newMessage));
    responseObserver.onCompleted();
  }

  @Override
  @PreAuthorize("hasAnyRole('admin')")
  public void search(SearchRequest request, StreamObserver<MessageResponse> responseObserver) {
    persistenceService.getMessagesWithFilename(request.getFilename(), request.getLimit())
        .map(ReactivePersistenceControllerGrpc::toMessageRespone)
        .doOnNext(responseObserver::onNext)
        .doOnComplete(responseObserver::onCompleted)
        .subscribe();
  }

}
