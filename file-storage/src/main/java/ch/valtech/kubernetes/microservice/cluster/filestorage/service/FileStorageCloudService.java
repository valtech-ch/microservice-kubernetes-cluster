package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import ch.valtech.kubernetes.microservice.cluster.filestorage.domain.FileArtifact;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Profile("cloud")
@Service
@Slf4j
public class FileStorageCloudService implements FileStorageService {
  
  private final BlobContainerClient containerClient;

  public FileStorageCloudService(
      @Value("${secret.storageAccountName}") String accountName,
      @Value("${secret.storageAccountKey}") String accountKey,
      @Value("${secret.storageConnection}") String connection,
      @Value("${secret.storageContainerName}") String containerName) {
    StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);
    
    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
        .connectionString(connection)
        .credential(credential)
        .buildClient();
    
    containerClient = blobServiceClient.createBlobContainer(containerName);
  }
  
  @Override
  public String saveFile(MultipartFile file) {
    return "not-implemented";
  }

  @Override
  public List<FileArtifact> loadAll() {
    return null;
  }

  @Override
  public Resource loadAsResource(String filename) {
    return null;
  }

  @Override
  public void deleteByFilename(String filename) {

  }

  @Override
  public void deleteAll() {

  }
}
