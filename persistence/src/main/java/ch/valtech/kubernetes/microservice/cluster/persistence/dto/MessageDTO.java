package ch.valtech.kubernetes.microservice.cluster.persistence.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class MessageDTO {

  String key;
  String message;

}
