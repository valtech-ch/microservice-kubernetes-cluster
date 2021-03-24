package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import ch.valtech.kubernetes.microservice.cluster.filestorage.domain.FileArtifact;
import ch.valtech.kubernetes.microservice.cluster.filestorage.exception.FileStorageException;
import ch.valtech.kubernetes.microservice.cluster.common.dto.Action;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.common.StorageSharedKeyCredential;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Profile("cloud")
@Service
@Slf4j
public class FileStorageCloudService implements FileStorageService {

  public static final String ALL_FILES = "ALL FILES";

  private final BlobContainerClient containerClient;
  private final AuditingService auditingService;

  public FileStorageCloudService(
      @Value("${application.cloud.storage.account.name}") String accountName,
      @Value("${application.cloud.storage.account.key}") String accountKey,
      @Value("${application.cloud.storage.connection}") String connection,
      @Value("${application.cloud.storage.container.name}") String containerName,
      AuditingService auditingService) {
    StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);

    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
        .connectionString(connection)
        .credential(credential)
        .buildClient();

    containerClient = blobServiceClient.getBlobContainerClient(containerName);
    this.auditingService = auditingService;

    try {
      containerClient.create();
    } catch (BlobStorageException error) {
      if (error.getErrorCode().equals(BlobErrorCode.CONTAINER_ALREADY_EXISTS)) {
        log.warn("Can't create container. It already exists");
      } else {
        throw error;
      }
    }
  }
  
  @Override
  public String saveFile(MultipartFile file) {
    String filename = file.getOriginalFilename();

    BlobClient blobClient = containerClient.getBlobClient(filename);

    // Upload the blob
    try (InputStream inputStream = file.getInputStream()) {
      blobClient.upload(inputStream, file.getSize(), true);
      auditingService.audit(filename, Action.UPLOAD);
      log.info("File {} added successfully to cloud storage", filename);
    } catch (IOException e) {
      String message = String.format("Failed to store file %s to cloud", filename);
      throw new FileStorageException(message, e);
    }
    return filename;
  }
  
  @Override
  public URL getResourceUrl(String filename) {
    try {
      BlobClient blobClient = containerClient.getBlobClient(filename);
      if (blobClient.exists()) {
        return new URL(blobClient.getBlobUrl());
      } else {
        throw new FileStorageException("File not found " + filename);
      }
    } catch (MalformedURLException ex) {
      throw new FileStorageException("Could not get resource URL");
    }
  }

  @Override
  public List<FileArtifact> loadAll() {
    log.info("Loading all files in cloud");
    return containerClient.listBlobs().stream()
        .map(blobItem -> FileArtifact.builder().filename(blobItem.getName()).build())
        .collect(Collectors.toList());
  }

  @Override
  public Resource loadAsResource(String filename) {
    log.info("Loading file {} from cloud", filename);
    BlobClient blobClient = containerClient.getBlobClient(filename);
    if (blobClient.exists()) {
      auditingService.audit(filename, Action.DOWNLOAD);
      return new InputStreamResource(blobClient.openInputStream(), filename);
    } else {
      throw new FileStorageException("File not found " + filename);
    }
  }

  @Override
  public void deleteByFilename(String filename) {
    log.info("Deleting file {} from cloud", filename);
    try {
      containerClient.getBlobClient(filename).delete();
    } catch (BlobStorageException ex) {
      if (ex.getErrorCode().equals(BlobErrorCode.BLOB_NOT_FOUND)) {
        throw new FileStorageException("File not found " + filename, ex);
      } else {
        throw ex;
      }
    }
    auditingService.audit(filename, Action.DELETE);
  }

  @Override
  public void deleteAll() {
    try {
      auditingService.audit(ALL_FILES, Action.DELETE);
      containerClient.delete();
    } catch (BlobStorageException ex) {
      throw new FileStorageException("Exception while deleting files");
    }
  }
  
}
