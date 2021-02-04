package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import ch.valtech.kubernetes.microservice.cluster.filestorage.domain.FileArtifact;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Profile("cloud")
@Service
public class FileStorageCloudService implements FileStorageService {

  @Override
  public String saveFile(MultipartFile file) {
    return "not-implemented";
  }

  @Override
  public List<FileArtifact> loadAll() {
    return null;
  }

  @Override
  public Resource loadAsResource(String filename) {
    return null;
  }

  @Override
  public void deleteByFilename(String filename) {

  }

  @Override
  public void deleteAll() {

  }
}
