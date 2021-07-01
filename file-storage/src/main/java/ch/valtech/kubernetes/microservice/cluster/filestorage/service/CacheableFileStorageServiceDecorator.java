package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import ch.valtech.kubernetes.microservice.cluster.filestorage.domain.FileArtifact;
import java.net.URL;
import java.util.List;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component("cacheable")
public class CacheableFileStorageServiceDecorator extends FileStorageServiceDecorator {

  private static final String FILES_CACHE_NAME = "files";

  private final CacheManager cacheManager;

  public CacheableFileStorageServiceDecorator(FileStorageService fileStorageService, CacheManager cacheManager) {
    super(fileStorageService);
    this.cacheManager = cacheManager;
  }

  @Override
  public String saveFile(MultipartFile file) {
    String fileName = fileStorageService.saveFile(file);
    evictCache();
    return fileName;
  }

  @Override
  @Cacheable(FILES_CACHE_NAME)
  public List<FileArtifact> loadAll() {
    return fileStorageService.loadAll();
  }

  @Override
  public Resource loadAsResource(String filename) {
    return fileStorageService.loadAsResource(filename);
  }

  @Override
  public URL getResourceUrl(String filename) {
    return fileStorageService.getResourceUrl(filename);
  }

  @Override
  public void deleteByFilename(String filename) {
    fileStorageService.deleteByFilename(filename);
    evictCache();
  }

  @Override
  public void deleteAll() {
    fileStorageService.deleteAll();
    evictCache();
  }

  private void evictCache() {
    cacheManager.getCache(FILES_CACHE_NAME).clear();
  }

}
