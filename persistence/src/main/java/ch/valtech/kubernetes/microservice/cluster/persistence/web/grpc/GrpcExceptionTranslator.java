package ch.valtech.kubernetes.microservice.cluster.persistence.web.grpc;

import io.grpc.Status;
import org.lognet.springboot.grpc.recovery.GRpcExceptionHandler;
import org.lognet.springboot.grpc.recovery.GRpcExceptionScope;
import org.lognet.springboot.grpc.recovery.GRpcServiceAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

@GRpcServiceAdvice
public class GrpcExceptionTranslator {

  @GRpcExceptionHandler
  public Status handleAccessDeniedException(AccessDeniedException e, GRpcExceptionScope scope) {
    return Status.PERMISSION_DENIED.withDescription("Insufficient permissions").withCause(e);
  }

  @GRpcExceptionHandler
  public Status handleResponseStatusException(ResponseStatusException e, GRpcExceptionScope scope) {
    if (HttpStatus.FORBIDDEN.value() == e.getStatusCode().value()) {
      return Status.PERMISSION_DENIED.withDescription(e.getReason()).withCause(e);
    } else {
      return Status.UNKNOWN.withDescription(e.getReason()).withCause(e);
    }
  }

}
