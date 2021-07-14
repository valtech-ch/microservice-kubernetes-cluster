package ch.valtech.kubernetes.microservice.cluster.persistence.config;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
public class KafkaConsumerConfiguration {

  private final String bootstrapAddress;
  private final String autoOffsetReset;
  private final String groupId;

  public KafkaConsumerConfiguration(
      @Value(value = "${application.kafka.bootstrapAddress}") String bootstrapAddress,
      @Value(value = "${application.kafka.autoOffsetReset}") String autoOffsetReset,
      @Value(value = "${application.kafka.groupId}") String groupId) {
    this.bootstrapAddress = bootstrapAddress;
    this.autoOffsetReset = autoOffsetReset;
    this.groupId = groupId;
  }

  @Bean
  public ConsumerFactory<String, AuditingRequestDto> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    ErrorHandlingDeserializer<AuditingRequestDto> errorHandlingDeserializer =
        new ErrorHandlingDeserializer<>(new JsonDeserializer<>(AuditingRequestDto.class));
    return new DefaultKafkaConsumerFactory<String, AuditingRequestDto>(props, new StringDeserializer(),
        errorHandlingDeserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, AuditingRequestDto> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, AuditingRequestDto> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setErrorHandler(new KafkaErrorHandler());
    return factory;
  }

}
