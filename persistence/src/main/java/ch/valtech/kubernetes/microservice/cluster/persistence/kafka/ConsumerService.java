package ch.valtech.kubernetes.microservice.cluster.persistence.kafka;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public final class ConsumerService {

  private final PersistenceService persistenceService;

  public ConsumerService(PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
  }

  @KafkaListener(topics = "${application.kafka.topic}", groupId = "${application.kafka.groupId}")
  public void consume(AuditingRequestDto message) {
    log.info("Consumed auditing message: {}", message);
    persistenceService.saveNewMessage(message);
  }
}
