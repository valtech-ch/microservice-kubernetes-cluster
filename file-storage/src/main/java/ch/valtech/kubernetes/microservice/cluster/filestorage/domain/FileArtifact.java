package ch.valtech.kubernetes.microservice.cluster.filestorage.domain;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder(toBuilder = true)
@Jacksonized
public class FileArtifact implements Serializable {

  @NotBlank(message = "Filename is mandatory")
  private String filename;

}
