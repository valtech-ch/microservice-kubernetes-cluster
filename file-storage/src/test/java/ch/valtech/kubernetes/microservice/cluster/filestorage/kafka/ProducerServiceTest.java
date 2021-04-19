package ch.valtech.kubernetes.microservice.cluster.filestorage.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.kafka.test.assertj.KafkaConditions.key;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

@Slf4j
@DirtiesContext
@SpringBootTest
@EmbeddedKafka(topics = "auditing", bootstrapServersProperty = "application.kafka.bootstrapAddress")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProducerServiceTest {

  public static final String FILENAME = "test.txt";
  private static String TOPIC = "auditing";

  @Autowired
  private ProducerService producerService;

  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;

  BlockingQueue<ConsumerRecord<String, String>> consumerRecords;

  KafkaMessageListenerContainer<String, AuditingRequestDto> container;

  @BeforeAll
  void setUp() {
    ContainerProperties containerProperties = new ContainerProperties(TOPIC);
    Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));
    DefaultKafkaConsumerFactory<String, AuditingRequestDto> consumerFactory = new DefaultKafkaConsumerFactory<>(
        configs);
    container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    consumerRecords = new LinkedBlockingQueue<>();
    container.setupMessageListener((MessageListener<String, String>) consumerRecords::add);
    container.start();
    ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
  }

  @AfterAll
  public void tearDown() {
    container.stop();
  }

  @Test
  public void shouldCheckMessageIsProduced() throws InterruptedException, IOException {
    //given
    AuditingRequestDto auditingRequestDto = AuditingRequestDto.builder()
        .filename(FILENAME)
        .action(Action.UPLOAD)
        .build();

    //when
    producerService.sendMessage(FILENAME, Action.UPLOAD);

    //then
    ConsumerRecord<String, String> received = consumerRecords.poll(10, TimeUnit.SECONDS);

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(auditingRequestDto);

    assertEquals(json, received.value());

    assertThat(received).has(key(null));
  }
}