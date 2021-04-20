package ch.valtech.kubernetes.microservice.cluster.filestorage.kafka;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
public class AuditingRequestDtoDeserializer implements Deserializer<AuditingRequestDto> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
  }

  @Override
  public AuditingRequestDto deserialize(String topic, byte[] data) {
    try {
      return objectMapper.readValue(new String(data, StandardCharsets.UTF_8), AuditingRequestDto.class);
    } catch (Exception e) {
      log.error("Unable to deserialize message {}", data, e);
      return null;
    }
  }

  @Override
  public void close() {
  }

}
