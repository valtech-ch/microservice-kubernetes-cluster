package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import ch.valtech.kubernetes.microservice.cluster.filestorage.domain.FileArtifact;
import ch.valtech.kubernetes.microservice.cluster.filestorage.exception.FileStorageException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Profile("!cloud")
@Service
public class FileStorageLocalService implements FileStorageService {

  @Value("${upload.path}")
  private String path;

  @Override
  public String saveFile(MultipartFile file) {
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

    return "url";
  }

  @Override
  public List<FileArtifact> loadAll() {
    try {
      return Files.walk(Paths.get(path))
          .filter(Files::isRegularFile)
          .map(path -> FileArtifact.builder().filename(path.getFileName().toString()).build())
          .collect(Collectors.toList());
    }  catch (IOException ex) {
      throw new FileStorageException("Files not found ", ex);
    }
  }

  @Override
  public Resource loadAsResource(String fileName) {
    try {
      Path filePath = Paths.get(path).resolve(fileName).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if(resource.exists()) {
        return resource;
      } else {
        throw new FileStorageException("File not found " + fileName);
      }
    } catch (MalformedURLException ex) {
      throw new FileStorageException("File not found " + fileName, ex);
    }
  }

  @Override
  public void deleteByFilename(String filename) {
    try {
      Files.deleteIfExists(Paths.get(path).resolve(filename).normalize());
    } catch (IOException ex) {
      throw new FileStorageException("File not found " + filename, ex);
    }
  }

  @Override
  public void deleteAll() {
    try {
      FileUtils.cleanDirectory(new File(path));
    } catch (IOException ex) {
      throw new FileStorageException("Exception while deleting files");
    }
  }

}
