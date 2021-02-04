package ch.valtech.kubernetes.microservice.cluster.persistence.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = MessageDTO.MessageDTOBuilder.class)

public class MessageDTO {
  String key;
  String message;
  
  @JsonPOJOBuilder(withPrefix = "")
  public static class MessageDTOBuilder {
  }
}
