package ch.valtech.kubernetes.microservice.cluster.persistence.kafka;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public final class ConsumerService {

  private final PersistenceService persistenceService;

  private final AuthenticationManager authenticationManager;

  public ConsumerService(PersistenceService persistenceService,
      AuthenticationManager authenticationManager) {
    this.persistenceService = persistenceService;
    this.authenticationManager = authenticationManager;
  }

  @KafkaListener(topics = "${application.kafka.topic}", groupId = "${application.kafka.groupId}")
  public void consume(@Payload AuditingRequestDto message, @Header("jwt") String token) {
    log.info("Consumed auditing message: {}", message);
    if (StringUtils.isNotBlank(token)) {
      Authentication authentication = authenticationManager
          .authenticate(new BearerTokenAuthenticationToken(token));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    persistenceService.saveNewMessage(message);
  }

}
