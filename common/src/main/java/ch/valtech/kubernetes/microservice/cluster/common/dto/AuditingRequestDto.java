package ch.valtech.kubernetes.microservice.cluster.common.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AuditingRequestDto {

  String filename;
  Action action;

}
