package ch.valtech.kubernetes.microservice.cluster.persistence.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class AuditingRequestDto {

  @NotBlank(message = "Filename is mandatory")
  String filename;

  @NotNull(message = "Action is mandatory")
  Action action;

}
