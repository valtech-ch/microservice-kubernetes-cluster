package ch.valtech.kubernetes.microservice.cluster.persistence.service;

import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PersistenceUtils {

  public static String createMessage(Auditing auditing) {
    switch (auditing.getAction()) {
      case UPLOAD:
        return String.format("User %s uploaded %s", auditing.getUsername(), auditing.getFilename());
      case DELETE:
        return String.format("User %s deleted %s", auditing.getUsername(), auditing.getFilename());
      case DOWNLOAD:
        return String.format("User %s downloaded %s", auditing.getUsername(), auditing.getFilename());
      default:
        return "No action detected";
    }
  }

}
