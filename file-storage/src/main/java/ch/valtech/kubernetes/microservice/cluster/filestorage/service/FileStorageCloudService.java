package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Profile("prod")
@Service
public class FileStorageCloudService implements FileStorageService {

  @Override
  public void saveFile(MultipartFile file) {

  }
}
