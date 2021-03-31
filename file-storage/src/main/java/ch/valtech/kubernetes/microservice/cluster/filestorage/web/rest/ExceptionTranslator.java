package ch.valtech.kubernetes.microservice.cluster.filestorage.web.rest;

import ch.valtech.kubernetes.microservice.cluster.filestorage.exception.FileStorageException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  @ExceptionHandler(value = FileStorageException.class)
  protected ResponseEntity<Object> handleInternalServerError(RuntimeException ex, WebRequest request) {
    return handleExceptionInternal(ex, ExceptionUtils.getMessage(ex),
        new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

}
