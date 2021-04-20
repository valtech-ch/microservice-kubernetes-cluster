package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

@Configuration
public class KafkaStreamConfiguration {

  private final String bootstrapAddress;

  public KafkaStreamConfiguration(@Value(value = "${application.kafka.bootstrapAddress}") String bootstrapAddress) {
    this.bootstrapAddress = bootstrapAddress;
  }

  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  public StreamsConfig defaultStreamsConfigs() {
    Map<String, Object> config = new HashMap<>();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "default");
    setDefaults(config);
    return new StreamsConfig(config);
  }

  private void setDefaults(Map<String, Object> config) {
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
  }

  @Bean("reverseStreamBuilder")
  public StreamsBuilderFactoryBean configureReverseStreamBuilder() {
    Map<String, Object> config = new HashMap<>();
    setDefaults(config);
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "reverse");
    return new StreamsBuilderFactoryBean(new KafkaStreamsConfiguration(config));
  }

}
