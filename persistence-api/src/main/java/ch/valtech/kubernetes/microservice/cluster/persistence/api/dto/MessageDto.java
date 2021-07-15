package ch.valtech.kubernetes.microservice.cluster.persistence.api.dto;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class MessageDto {

  @NotBlank(message = "Message is mandatory")
  String message;

}
