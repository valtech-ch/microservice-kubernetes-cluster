package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import static ch.valtech.kubernetes.microservice.cluster.filestorage.util.FileNameCleaner.cleanFilename;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import ch.valtech.kubernetes.microservice.cluster.filestorage.domain.FileArtifact;
import ch.valtech.kubernetes.microservice.cluster.filestorage.exception.FileStorageException;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.common.StorageSharedKeyCredential;
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
import org.springframework.web.server.ResponseStatusException;

@Profile("cloud")
@Service
@Slf4j
public class FileStorageCloudService implements FileStorageService {

  public static final String FILE_NOT_FOUND = "File %s not found";
  private final BlobContainerClient containerClient;

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

    containerClient = blobServiceClient.getBlobContainerClient(containerName);

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
    String filename = cleanFilename(file.getOriginalFilename());

    BlobClient blobClient = containerClient.getBlobClient(filename);

    // Upload the blob
    try (InputStream inputStream = file.getInputStream()) {
      blobClient.upload(inputStream, file.getSize(), true);
      log.info("File {} added successfully to cloud storage", filename);
    } catch (Exception e) {
      String message = String.format("Failed to store file %s to cloud", filename);
      throw new FileStorageException(message, e);
    }
    return filename;
  }

  @Override
  public URL getResourceUrl(String filename) {
    String cleanFilename = cleanFilename(filename);
    try {
      BlobClient blobClient = containerClient.getBlobClient(cleanFilename);
      if (TRUE.equals(blobClient.exists())) {
        return new URL(blobClient.getBlobUrl());
      } else {
        throw new ResponseStatusException(NOT_FOUND, format(FILE_NOT_FOUND, filename));
      }
    } catch (MalformedURLException ex) {
      throw new FileStorageException("Could not get resource URL", ex);
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
    BlobClient blobClient = containerClient.getBlobClient(cleanFilename(filename));
    if (TRUE.equals(blobClient.exists())) {
      return new InputStreamResource(blobClient.openInputStream(), cleanFilename(filename));
    } else {
      throw new ResponseStatusException(NOT_FOUND, format(FILE_NOT_FOUND, filename));
    }
  }

  @Override
  public void deleteByFilename(String filename) {
    log.info("Deleting file {} from cloud", filename);
    try {
      containerClient.getBlobClient(cleanFilename(filename)).delete();
    } catch (BlobStorageException ex) {
      if (ex.getErrorCode().equals(BlobErrorCode.BLOB_NOT_FOUND)) {
        throw new ResponseStatusException(NOT_FOUND, format(FILE_NOT_FOUND, filename));
      } else {
        throw ex;
      }
    }
  }

  @Override
  public void deleteAll() {
    try {
      containerClient.listBlobs().stream()
          .forEach(blobItem -> containerClient.getBlobClient(blobItem.getName()).delete());
    } catch (BlobStorageException ex) {
      throw new FileStorageException("Exception while deleting files", ex);
    }
  }

}
