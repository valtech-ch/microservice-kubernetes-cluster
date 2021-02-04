package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import ch.valtech.kubernetes.microservice.cluster.filestorage.domain.FileArtifact;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

  String saveFile(MultipartFile file);

  List<FileArtifact> loadAll();

  Resource loadAsResource(String filename);

  void deleteByFilename(String filename);

  void deleteAll();
}
