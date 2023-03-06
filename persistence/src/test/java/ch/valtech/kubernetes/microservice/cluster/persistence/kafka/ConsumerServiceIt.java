package ch.valtech.kubernetes.microservice.cluster.persistence.kafka;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.valtech.kubernetes.microservice.cluster.persistence.AbstractIt;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import reactor.core.publisher.Mono;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConsumerServiceIt extends AbstractIt {

  public static final String FILENAME = "test.txt";

  @Value("${application.kafka.topic}")
  private String auditingTopic;

  @Value("${application.kafka.stream.topic}")
  private String auditingReverseTopic;

  @SpyBean
  private ConsumerService consumer;

  @MockBean
  private PersistenceService persistenceService;

  private String testToken;

  @Autowired
  private KafkaTemplate<String, AuditingRequestDto> kafkaTemplate;

  @BeforeEach
  @SneakyThrows
  void setUpProducer() {
    testToken = IOUtils.toString(getClass().getResourceAsStream("/test-token"));
  }

  @Test
  void testConsumeTopic() {
    //given
    String username = "vtc-keycloakadmin";
    AuditingRequestDto auditingRequestDto = AuditingRequestDto.builder()
        .filename(FILENAME)
        .action(Action.UPLOAD)
        .build();
    when(persistenceService.saveNewMessage(auditingRequestDto, username))
        .thenReturn(Mono.just(MessageDto.builder().build()));

    ProducerRecord<String, AuditingRequestDto> producerRecord = new ProducerRecord<>(auditingTopic,
        auditingRequestDto);
    producerRecord.headers().add("jwt", testToken.getBytes(UTF_8));

    //when
    kafkaTemplate.send(producerRecord);

    //then
    verify(consumer, timeout(10000).times(1))
        .consumeTopic(auditingRequestDto, testToken);
    verify(persistenceService, timeout(10000).times(1))
        .saveNewMessage(auditingRequestDto, username);
  }

  @Test
  void consumeStreamTopic() {
    //given
    AuditingRequestDto auditingRequestDto = AuditingRequestDto.builder()
        .filename(FILENAME)
        .action(Action.UPLOAD)
        .build();

    ProducerRecord<String, AuditingRequestDto> producerRecord = new ProducerRecord<>(auditingReverseTopic,
        auditingRequestDto);
    producerRecord.headers().add("jwt", testToken.getBytes(UTF_8));

    //when
    kafkaTemplate.send(producerRecord);

    //then
    verify(consumer, timeout(10000).times(1))
        .consumeStreamTopic(auditingRequestDto, testToken);
    verify(persistenceService, timeout(10000).times(0))
        .saveNewMessage(eq(auditingRequestDto), anyString());
  }

}
