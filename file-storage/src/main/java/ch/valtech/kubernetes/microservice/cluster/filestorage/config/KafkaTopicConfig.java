package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

  private String bootstrapAddress;
  private String topic;

  public KafkaTopicConfig(
      @Value(value = "${kafka.bootstrapAddress}") String bootstrapAddress,
      @Value(value = "${kafka.topic}") String topic) {
    this.bootstrapAddress = bootstrapAddress;
    this.topic = topic;
  }

  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    return new KafkaAdmin(configs);
  }

  @Bean
  public NewTopic auditTopic() {
    return new NewTopic(topic, 1, (short) 1);
  }
}
