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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Profile("dev")
@Service
@Slf4j
public class FileStorageCloudService implements FileStorageService {
  
  private BlobContainerClient containerClient;

  public FileStorageCloudService(
      @Value("${storageAccountName}") String accountName,
      @Value("${secret.storageAccountKey}") String accountKey,
      @Value("${secret.storageConnection}") String connection,
      @Value("${storageContainerName}") String containerName) {
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
    return null;
  }

  @Override
  public List<FileArtifact> loadAll() {
    //todo should return even folders - check structure
    return containerClient.listBlobs().stream()
        .map(blobItem -> FileArtifact.builder().filename(blobItem.getName()).build())
        .collect(Collectors.toList());
  }

  @Override
  public Resource loadAsResource(String filename) {
    log.info("Loading file {} from cloud", filename);
    String path = "/tmp/cloud/downloads/";
    try {
      BlobClient blobClient = containerClient.getBlobClient(filename); //todo move to class
      Files.createDirectories(Paths.get(path));
      blobClient.downloadToFile(path + filename);
      Path filePath = Paths.get(path).resolve(filename).normalize();
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
    containerClient.getBlobClient(filename).delete(); //todo to be checked
  }

  @Override
  public void deleteAll() {
    containerClient.delete();
  }
  
}
