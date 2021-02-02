package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

  void saveFile(MultipartFile file);
}
