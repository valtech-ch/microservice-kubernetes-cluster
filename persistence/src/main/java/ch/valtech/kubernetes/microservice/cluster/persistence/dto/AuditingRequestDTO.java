package ch.valtech.kubernetes.microservice.cluster.persistence.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = AuditingRequestDTO.AuditingRequestDTOBuilder.class)
public class AuditingRequestDTO {
  String email;
  String key;
  String messageValue;
  
  @JsonPOJOBuilder(withPrefix = "")
  public static class AuditingRequestDTOBuilder {
  }
}
