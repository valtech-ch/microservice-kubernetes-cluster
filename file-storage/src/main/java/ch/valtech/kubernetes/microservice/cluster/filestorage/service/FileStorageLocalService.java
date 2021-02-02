package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import ch.valtech.kubernetes.microservice.cluster.filestorage.exception.FileStorageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Profile("dev")
@Service
public class FileStorageLocalService implements FileStorageService {

  @Value("${upload.path}")
  private String path;

  @Override
  public void saveFile(MultipartFile file) {
    if (file.isEmpty()) {
      throw new FileStorageException("File should not be empty");
    }

    try {
      String fileName = file.getOriginalFilename();
      InputStream inputStream = file.getInputStream();

      Files.createDirectories(Paths.get(path));
      Files.copy(inputStream, Paths.get(path + fileName),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      String message = String.format("Failed to store file %s", file.getName());
      throw new FileStorageException(message, e);
    }
  }

}
