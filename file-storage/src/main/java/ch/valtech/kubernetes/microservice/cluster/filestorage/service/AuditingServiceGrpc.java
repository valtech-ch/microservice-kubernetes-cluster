package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.AuditingRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.ReactorPersistenceServiceGrpc.ReactorPersistenceServiceStub;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.SearchRequest;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

@Slf4j
@Service("auditingServiceGrpc")
public class AuditingServiceGrpc implements AuditingService {

  @GrpcClient("persistence")
  private ReactorPersistenceServiceStub persistenceStub;

  @Override
  public MessageDto audit(String filename, Action action) {
    AuditingRequest test = AuditingRequest.newBuilder()
        .setFilename(filename)
        .setAction(AuditingRequest.Action.valueOf(action.toString()))
        .build();

    try {
      return MessageDto.builder()
          .message(persistenceStub.audit(test).block().getMessage())
          .build();
    } catch (Exception e) {
      Status status = Status.fromThrowable(e);
      switch (status.getCode()) {
        case UNAVAILABLE:
          throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Persistence service unavailable");
        case PERMISSION_DENIED:
          throw new ResponseStatusException(FORBIDDEN, status.getDescription());
        default:
          log.error("Audit failed with status {} : {}", status.getCode(), status.getDescription());
          throw new ResponseStatusException(INTERNAL_SERVER_ERROR, status.getDescription());
      }
    }
  }

  @Override
  public Flux<MessageDto> search(SearchRequest searchRequest) {
    return persistenceStub.search(searchRequest).map(messageResponse -> MessageDto.builder()
        .message(messageResponse.getMessage())
        .build());
  }

}
