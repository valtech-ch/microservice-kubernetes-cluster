package ch.valtech.kubernetes.microservice.cluster.filestorage.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class FileArtifact {
  private String filename;
}
