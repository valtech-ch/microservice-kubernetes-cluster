package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import ch.valtech.kubernetes.microservice.cluster.filestorage.domain.FileArtifact;
import ch.valtech.kubernetes.microservice.cluster.filestorage.exception.FileStorageException;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.common.StorageSharedKeyCredential;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Profile("cloud")
@Service
@Slf4j
public class FileStorageCloudService implements FileStorageService {

  public static final String TMP_STORAGE_DOWNLOAD = "/tmp/file-storage/cloud/downloads/";

  private BlobContainerClient containerClient;

  public FileStorageCloudService(
      @Value("${application.cloud.storage.account.name}") String accountName,
      @Value("${application.cloud.storage.account.key}") String accountKey,
      @Value("${application.cloud.storage.connection}") String connection,
      @Value("${application.cloud.storage.container.name}") String containerName) {
    StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);
    
    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
        .connectionString(connection)
        .credential(credential)
        .buildClient();

    try {
      containerClient = blobServiceClient.createBlobContainer(containerName);
    } catch (BlobStorageException error) {
      if (error.getErrorCode().equals(BlobErrorCode.CONTAINER_ALREADY_EXISTS)) {
        log.warn("Can't create container. It already exists");
        containerClient = blobServiceClient.getBlobContainerClient(containerName);
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
      if (blobClient.exists()){
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
    try {
      BlobClient blobClient = containerClient.getBlobClient(filename);
      //persist to tmp directory
      Files.createDirectories(Paths.get(TMP_STORAGE_DOWNLOAD));
      FileUtils.cleanDirectory(new File(TMP_STORAGE_DOWNLOAD));
      blobClient.downloadToFile(TMP_STORAGE_DOWNLOAD + filename);

      Path filePath = Paths.get(TMP_STORAGE_DOWNLOAD).resolve(filename).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if(resource.exists()) {
        return resource;
      } else {
        throw new FileStorageException("File not found " + filename);
      }
    } catch (IOException ex) {
      throw new FileStorageException("File not found " + filename, ex);
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
  }

  @Override
  public void deleteAll() {
    try {
      containerClient.delete();
    } catch (BlobStorageException ex) {
      throw new FileStorageException("Exception while deleting files");
    }
  }
  
}
