package ch.valtech.kubernetes.microservice.cluster.persistence.api.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class AuditingRequestDto {

  String filename;
  Action action;

}
