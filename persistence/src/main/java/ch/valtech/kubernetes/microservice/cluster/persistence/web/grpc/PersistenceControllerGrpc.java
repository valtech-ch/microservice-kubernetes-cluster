package ch.valtech.kubernetes.microservice.cluster.persistence.web.grpc;

import static ch.valtech.kubernetes.microservice.cluster.persistence.util.SecurityUtils.getUsername;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.AuditingRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.MessageResponse;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.PersistenceServiceGrpc;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.SearchRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.mapper.PersistenceMapper;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
// @GrpcService using ReactivePersistenceControllerGrpc
public class PersistenceControllerGrpc extends PersistenceServiceGrpc.PersistenceServiceImplBase {

  private final PersistenceService persistenceService;
  private final PersistenceMapper persistenceMapper;

  public PersistenceControllerGrpc(PersistenceService persistenceService,
      PersistenceMapper persistenceMapper) {
    this.persistenceService = persistenceService;
    this.persistenceMapper = persistenceMapper;
  }

  @Override
  @PreAuthorize("hasAnyRole('admin', 'user')")
  public void audit(AuditingRequest request, StreamObserver<MessageResponse> responseObserver) {
    log.info("gRPC Received!");
    String username = getUsername().orElseThrow(() ->
        new ResponseStatusException(FORBIDDEN, "Username not found"));
    MessageDto newMessage = persistenceService
        .saveNewMessage(persistenceMapper.toAuditingRequestDto(request), username)
        .block();

    responseObserver.onNext(persistenceMapper.toMessageResponse(newMessage));
    responseObserver.onCompleted();
  }

  @Override
  @PreAuthorize("hasAnyRole('admin')")
  public void search(SearchRequest request, StreamObserver<MessageResponse> responseObserver) {
    persistenceService.getMessagesWithFilename(request.getFilename(), request.getLimit())
        .map(persistenceMapper::toMessageResponse)
        .doOnNext(responseObserver::onNext)
        .doOnComplete(responseObserver::onCompleted)
        .subscribe();
  }

}
