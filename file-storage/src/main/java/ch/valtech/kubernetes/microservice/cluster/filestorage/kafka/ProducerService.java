package ch.valtech.kubernetes.microservice.cluster.filestorage.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public final class ProducerService {

  private final KafkaTemplate<String, String> kafkaTemplate;

  private final String topic;

  public ProducerService(KafkaTemplate<String, String> kafkaTemplate,
      @Value(value = "${kafka.topic}") String topic) {
    this.kafkaTemplate = kafkaTemplate;
    this.topic = topic;
  }

  public void sendMessage(String message) {
    log.info(String.format("Producing message: %s", message));

    this.kafkaTemplate.send(topic, message);
  }
}
