package ch.valtech.kubernetes.microservice.cluster.releasetoggle.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ReleaseToggleNotEnabledException extends RuntimeException {

  public ReleaseToggleNotEnabledException(String releaseToggleName) {
    super("Release toggle " + releaseToggleName + " is not enabled");
  }

}
