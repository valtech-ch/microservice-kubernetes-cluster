package ch.valtech.kubernetes.microservice.cluster.persistence.web.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures. The error response
 * follows RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807).
 */
@ControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = AccessDeniedException.class)
  protected ResponseEntity<Object> handleInternalForbidden(RuntimeException ex, WebRequest request) {
    return handleExceptionInternal(ex, "Forbidden",
        new HttpHeaders(), HttpStatus.FORBIDDEN, request);
  }

}
