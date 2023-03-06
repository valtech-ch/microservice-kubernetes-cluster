package ch.valtech.kubernetes.microservice.cluster.persistence.kafka;

import static ch.valtech.kubernetes.microservice.cluster.persistence.util.SecurityUtils.getUsername;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class ConsumerService {

  private final PersistenceService persistenceService;

  public ConsumerService(PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
  }

  @KafkaListener(topics = "${application.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
  public void consumeTopic(@Payload AuditingRequestDto message, @Header("jwt") String token) {
    log.info("Consumed auditing message: {}", message);
    // TODO
    //if (StringUtils.isNotBlank(token)) {
    //  Authentication authentication = authenticationManager
    //      .authenticate(new BearerTokenAuthenticationToken(token));
    //  SecurityContextHolder.getContext().setAuthentication(authentication);
    //}
    //String username = getUsername().orElseThrow(() ->
    //    new ResponseStatusException(FORBIDDEN, "Username not found"));
    persistenceService.saveNewMessage(message, "vtc-keycloakadmin").block();
  }

  @KafkaListener(topics = "${application.kafka.stream.topic}", groupId = "${spring.kafka.consumer.group-id}")
  public void consumeStreamTopic(@Payload AuditingRequestDto message, @Header("jwt") String token) {
    // TODO
    //if (StringUtils.isNotBlank(token)) {
    //  Authentication authentication = authenticationManager
    //      .authenticate(new BearerTokenAuthenticationToken(token));
    //  SecurityContextHolder.getContext().setAuthentication(authentication);
    //}
    log.info("Consumed reversed stream filename: {}", message.getFilename());
  }

}
