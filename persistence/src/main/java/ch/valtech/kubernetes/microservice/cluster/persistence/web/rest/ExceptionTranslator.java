package ch.valtech.kubernetes.microservice.cluster.persistence.web.rest;

import static org.apache.commons.lang3.exception.ExceptionUtils.getMessage;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

  @ExceptionHandler(AccessDeniedException.class)
  ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(FORBIDDEN, getMessage(ex));
    problemDetail.setTitle("Forbidden");
    return problemDetail;
  }

}
