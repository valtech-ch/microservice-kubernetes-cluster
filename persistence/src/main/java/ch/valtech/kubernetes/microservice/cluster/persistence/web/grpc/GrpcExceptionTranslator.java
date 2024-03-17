package ch.valtech.kubernetes.microservice.cluster.persistence.web.grpc;

import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

@GrpcAdvice
public class GrpcExceptionTranslator {

  @GrpcExceptionHandler
  public Status handleAccessDeniedException(AccessDeniedException e) {
    return Status.PERMISSION_DENIED.withDescription("Insufficient permissions").withCause(e);
  }

  @GrpcExceptionHandler
  public Status handleResponseStatusException(ResponseStatusException e) {
    if (HttpStatus.FORBIDDEN.value() == e.getStatusCode().value()) {
      return Status.PERMISSION_DENIED.withDescription(e.getReason()).withCause(e);
    } else {
      return Status.UNKNOWN.withDescription(e.getReason()).withCause(e);
    }
  }

}
