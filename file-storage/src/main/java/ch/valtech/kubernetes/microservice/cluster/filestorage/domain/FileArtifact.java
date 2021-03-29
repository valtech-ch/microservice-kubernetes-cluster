package ch.valtech.kubernetes.microservice.cluster.filestorage.domain;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class FileArtifact {
  private String filename;
}
