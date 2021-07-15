package ch.valtech.kubernetes.microservice.cluster.persistence.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
