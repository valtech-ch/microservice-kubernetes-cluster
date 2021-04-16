package ch.valtech.kubernetes.microservice.cluster.persistence.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Service
@RestController
public final class ConsumerService {

  public static final String TOPIC = "auditing";
  public static final String GROUP_ID = "persistenceApp";

  @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
  public void consume(String message) {
    log.info(String.format("$$$$ => Consumed message: %s", message));
  }
}
