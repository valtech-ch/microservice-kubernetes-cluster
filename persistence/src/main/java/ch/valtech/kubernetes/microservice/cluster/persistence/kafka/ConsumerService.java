package ch.valtech.kubernetes.microservice.cluster.persistence.kafka;

import static ch.valtech.kubernetes.microservice.cluster.persistence.util.SecurityUtils.decode;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public final class ConsumerService {

  private final PersistenceService persistenceService;

  public ConsumerService(PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
  }

  @KafkaListener(topics = "${application.kafka.topic}", groupId = "${application.kafka.groupId}")
  public void consume(@Payload AuditingRequestDto message, @Header("jwt") String token) {
    log.info("Consumed auditing message: {}", message);
    JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(decode(token));
    SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
    persistenceService.saveNewMessage(message);
  }
}
