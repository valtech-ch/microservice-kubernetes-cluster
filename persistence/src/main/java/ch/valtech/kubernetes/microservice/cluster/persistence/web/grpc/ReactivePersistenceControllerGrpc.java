package ch.valtech.kubernetes.microservice.cluster.persistence.web.grpc;

import static ch.valtech.kubernetes.microservice.cluster.persistence.util.SecurityUtils.getUsername;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.AuditingRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.MessageResponse;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.ReactorPersistenceServiceGrpc;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.SearchRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.mapper.PersistenceMapper;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@GrpcService
public class ReactivePersistenceControllerGrpc extends ReactorPersistenceServiceGrpc.PersistenceServiceImplBase {

  private final PersistenceService persistenceService;
  private final PersistenceMapper persistenceMapper;

  public ReactivePersistenceControllerGrpc(PersistenceService persistenceService,
      PersistenceMapper persistenceMapper) {
    this.persistenceService = persistenceService;
    this.persistenceMapper = persistenceMapper;
  }

  @Override
  @PreAuthorize("hasAnyRole('admin', 'user')")
  public Mono<MessageResponse> audit(Mono<AuditingRequest> request) {
    log.info("gRPC Received!");
    String username = getUsername().orElseThrow(() ->
        new ResponseStatusException(FORBIDDEN, "Username not found"));
    return request.map(persistenceMapper::toAuditingRequestDto)
        .flatMap(auditingRequestDto -> persistenceService.saveNewMessage(auditingRequestDto, username))
        .map(persistenceMapper::toMessageResponse);
  }

  @Override
  @PreAuthorize("hasAnyRole('admin')")
  public Flux<MessageResponse> search(Mono<SearchRequest> request) {
    return request.flatMapMany(searchRequest -> persistenceService
        .getMessagesWithFilename(searchRequest.getFilename(), searchRequest.getLimit()))
        .map(persistenceMapper::toMessageResponse);
  }

}
