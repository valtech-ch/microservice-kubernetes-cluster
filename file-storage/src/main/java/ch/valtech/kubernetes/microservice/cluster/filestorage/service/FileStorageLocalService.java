package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import static java.lang.String.format;

import ch.valtech.kubernetes.microservice.cluster.filestorage.domain.FileArtifact;
import ch.valtech.kubernetes.microservice.cluster.filestorage.exception.FileStorageException;
import ch.valtech.kubernetes.microservice.cluster.common.dto.Action;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Profile("!cloud")
@Slf4j
@Service
public class FileStorageLocalService implements FileStorageService {

  public static final String ALL_FILES = "ALL FILES";

  private final String hostname;
  private final String uploadPath;
  private final AuditingService auditingService;

  public FileStorageLocalService(
      @Value("${application.hostname}") String hostname,
      @Value("${application.upload.path}") String uploadPath,
      AuditingService auditingService) {
    this.hostname = hostname;
    this.uploadPath = uploadPath;
    this.auditingService = auditingService;
  }

  @Override
  public String saveFile(MultipartFile file) {
    log.info("Adding a new file to path: {}", uploadPath);
    if (file.isEmpty()) {
      throw new FileStorageException("File should not be empty");
    }

    String filename = file.getOriginalFilename();
    try (InputStream inputStream = file.getInputStream()) {

      Files.createDirectories(Paths.get(uploadPath));
      Files.copy(inputStream, Paths.get(uploadPath + filename),
          StandardCopyOption.REPLACE_EXISTING);
      auditingService.audit(filename, Action.UPLOAD);
      log.info("File {} added successfully", filename);
    } catch (IOException e) {
      String message = String.format("Failed to store file %s", file.getName());
      throw new FileStorageException(message, e);
    }
    return filename;
  }
  
  @Override
  @SneakyThrows
  public URL getResourceUrl(String filename) {
    if (!Files.exists(Paths.get(uploadPath).resolve(filename).normalize())) {
      throw new FileStorageException(format("File %s not found", filename));
    }
    return new URL(hostname + format("/api/file/%s", filename));
  }
  
  @Override
  public List<FileArtifact> loadAll() {
    log.info("Loading all files in: {}", uploadPath);
    try (Stream<Path> paths = Files.walk(Paths.get(uploadPath))) {
      return paths.filter(Files::isRegularFile)
          .map(path -> FileArtifact.builder().filename(path.getFileName().toString()).build())
          .collect(Collectors.toList());
    }  catch (IOException ex) {
      throw new FileStorageException("Files not found ", ex);
    }
  }

  @Override
  public Resource loadAsResource(String filename) {
    log.info("Loading file {} from: {}", filename, uploadPath);
    try {
      Path filePath = Paths.get(uploadPath).resolve(filename).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists()) {
        auditingService.audit(filename, Action.DOWNLOAD);
        return resource;
      } else {
        throw new FileStorageException("File not found " + filename);
      }
    } catch (MalformedURLException ex) {
      throw new FileStorageException("File not found " + filename, ex);
    }
  }

  @Override
  public void deleteByFilename(String filename) {
    log.info("Deleting file {} from: {}", filename, uploadPath);
    try {
      Files.deleteIfExists(Paths.get(uploadPath).resolve(filename).normalize());
    } catch (IOException ex) {
      throw new FileStorageException("File not found " + filename, ex);
    }
    auditingService.audit(filename, Action.DELETE);
  }

  @Override
  public void deleteAll() {
    try {
      auditingService.audit(ALL_FILES, Action.DELETE);
      FileUtils.cleanDirectory(new File(uploadPath));
    } catch (IOException ex) {
      throw new FileStorageException("Exception while deleting files");
    }
  }

}
