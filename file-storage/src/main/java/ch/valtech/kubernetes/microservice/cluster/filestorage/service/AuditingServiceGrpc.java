package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import static net.devh.boot.grpc.client.security.CallCredentialsHelper.authorizationHeader;

import ch.valtech.kubernetes.microservice.cluster.filestorage.util.SecurityUtils;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.AuditingRequest;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.PersistenceServiceGrpc.PersistenceServiceBlockingStub;
import io.grpc.Status;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service("auditingServiceGrpc")
public class AuditingServiceGrpc implements AuditingService {

  @GrpcClient("persistence")
  private PersistenceServiceBlockingStub persistenceStub;

  @Override
  public MessageDto audit(String filename, Action action) {
    AuditingRequest test = AuditingRequest.newBuilder()
        .setFilename(filename)
        .setAction(AuditingRequest.Action.valueOf(action.toString()))
        .build();

    Optional<String> jwtToken = SecurityUtils.getJwt();
    PersistenceServiceBlockingStub stub = persistenceStub;
    if (jwtToken.isPresent()) {
      stub = persistenceStub.withCallCredentials(authorizationHeader("Bearer " + jwtToken.get()));
    }

    String message;
    try {
      message = stub.audit(test).getMessage();
    } catch (Exception e) {
      Status status = Status.fromThrowable(e);
      log.error("Audit failed with status {} : {}", status.getCode(), status.getDescription());
      message = status.getDescription();
    }

    return MessageDto.builder()
        .message(message)
        .build();
  }

}
