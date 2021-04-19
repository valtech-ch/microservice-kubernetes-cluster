package ch.valtech.kubernetes.microservice.cluster.persistence.kafka;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

@Slf4j
@DirtiesContext
@SpringBootTest
@EmbeddedKafka(topics = "auditing", bootstrapServersProperty = "application.kafka.bootstrapAddress")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConsumerServiceTest {

  public static final String FILENAME = "test.txt";
  private static String TOPIC = "auditing";

  private ConsumerService consumerService;

  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Mock
  private PersistenceService persistenceService;


  @BeforeAll
  void setUp() {
    consumerService = new ConsumerService(persistenceService);
  }

  @Test
  public void shouldCheckMessageIsProduced() {
    //given
    AuditingRequestDto auditingRequestDto = AuditingRequestDto.builder()
        .filename(FILENAME)
        .action(Action.UPLOAD)
        .build();
    when(persistenceService.saveNewMessage(eq(auditingRequestDto))).thenReturn(MessageDto.builder().build());
    Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
    Producer<String, String> producer = new DefaultKafkaProducerFactory<String, String>(configs)
        .createProducer();

    producer.send(new ProducerRecord<>(TOPIC, null, "{\"filename\":\"test.txt\",\"action\":\"UPLOAD\"}"));
    producer.flush();

    //when
    consumerService.consume(auditingRequestDto);

    //then
    verify(persistenceService, times(1)).saveNewMessage(auditingRequestDto);

  }
}