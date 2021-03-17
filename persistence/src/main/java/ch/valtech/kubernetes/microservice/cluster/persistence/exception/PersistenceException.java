package ch.valtech.kubernetes.microservice.cluster.persistence.exception;

public class PersistenceException extends RuntimeException {

  public PersistenceException(String message) {
    super(message);
  }

  public PersistenceException(String message, Throwable cause) {
    super(message, cause);
  }

}
