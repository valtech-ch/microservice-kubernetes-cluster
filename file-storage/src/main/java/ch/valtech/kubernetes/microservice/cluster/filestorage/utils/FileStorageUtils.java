package ch.valtech.kubernetes.microservice.cluster.filestorage.utils;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileStorageUtils {

  public static Optional<String> getToken() {
    return Optional.ofNullable((OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication())
        .map(authentication -> {
          if (authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
            return details.getTokenValue();
          }
          return null;
        });
  }
}
