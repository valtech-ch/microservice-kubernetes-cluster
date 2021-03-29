package ch.valtech.kubernetes.microservice.cluster.persistence.service;

import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PersistenceUtils {

  public static Optional<String> getUsername() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    return Optional.ofNullable(securityContext.getAuthentication())
        .map(authentication -> {
          if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            return springSecurityUser.getUsername();
          } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
          }
          return null;
        });
  }

  public static String createMessage(Auditing auditing) {
    switch (auditing.getAction()) {
      case UPLOAD:
        return String.format("User %s uploaded %s", auditing.getUsername(), auditing.getFilename());
      case DELETE:
        return String.format("User %s downloaded %s", auditing.getUsername(), auditing.getFilename());
      case DOWNLOAD:
        return String.format("User %s deleted %s", auditing.getUsername(), auditing.getFilename());
      default:
        return "No action detected";
    }
  }
}
