package ch.valtech.kubernetes.microservice.cluster.filestorage.kafka;

import ch.valtech.kubernetes.microservice.cluster.filestorage.service.FunctionsService;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ReverseStream {

  private final String inputTopic;
  private final String outputTopic;
  private final FunctionsService functionsService;

  public ReverseStream(
      @Value(value = "${application.kafka.topic}") String inputTopic,
      @Value(value = "${application.kafka.stream.topic}") String outputTopic,
      FunctionsService functionsService) {
    this.inputTopic = inputTopic;
    this.outputTopic = outputTopic;
    this.functionsService = functionsService;
  }

  @Bean("reverseStreamTopology")
  public KStream<String, AuditingRequestDto> startProcessing(
      @Qualifier("reverseStreamBuilder") StreamsBuilder builder,
      @Qualifier("auditingRequestSerde") Serde<AuditingRequestDto> auditingRequestSerde) {
    KStream<String, AuditingRequestDto> toReverse = builder
        .stream(inputTopic, Consumed.with(Serdes.String(), auditingRequestSerde));

    toReverse.map((key, value) -> {
      return new KeyValue<>(key, AuditingRequestDto.builder()
          .action(value.getAction())
          .filename(functionsService.reverse(value.getFilename()))
          .build());
    }).to(outputTopic, Produced.with(Serdes.String(), auditingRequestSerde));
    return toReverse;
  }

}
