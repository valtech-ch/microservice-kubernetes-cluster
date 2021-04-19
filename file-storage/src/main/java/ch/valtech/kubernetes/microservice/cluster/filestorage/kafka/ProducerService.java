package ch.valtech.kubernetes.microservice.cluster.filestorage.kafka;

import ch.valtech.kubernetes.microservice.cluster.filestorage.util.SecurityUtils;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public final class ProducerService {

  public static final String JWT = "jwt";
  private final KafkaTemplate<String, AuditingRequestDto> kafkaTemplate;

  private final String topic;

  public ProducerService(KafkaTemplate<String, AuditingRequestDto> kafkaTemplate,
      @Value(value = "${application.kafka.topic}") String topic) {
    this.kafkaTemplate = kafkaTemplate;
    this.topic = topic;
  }

  public void sendMessage(String filename, Action action) {
    AuditingRequestDto auditingRequest = AuditingRequestDto.builder()
        .filename(filename)
        .action(action).build();
    log.info("Producing auditing entry: {}", auditingRequest);
    ProducerRecord<String, AuditingRequestDto> producerRecord = new ProducerRecord<>(topic, auditingRequest);
    SecurityUtils.getJwt()
        .ifPresent(jwt -> producerRecord.headers().add(JWT, jwt.getBytes(StandardCharsets.UTF_8)));
    this.kafkaTemplate.send(producerRecord);
  }
}
