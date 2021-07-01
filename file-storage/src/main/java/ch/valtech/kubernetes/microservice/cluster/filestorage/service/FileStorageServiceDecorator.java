package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

public abstract class FileStorageServiceDecorator implements FileStorageService {

  protected final FileStorageService fileStorageService;

  public FileStorageServiceDecorator(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
  }

}
