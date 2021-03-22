package ch.valtech.kubernetes.microservice.cluster.filestorage.web.rest;

import ch.valtech.kubernetes.microservice.cluster.filestorage.domain.FileArtifact;
import ch.valtech.kubernetes.microservice.cluster.filestorage.service.FileStorageService;
import java.net.URL;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for managing Files.
 */
@Slf4j
@Controller
@RequestMapping("/api")
public class FileStorageController {

  private final FileStorageService fileStorageService;

  public FileStorageController(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
  }

  /**
   * {@code POST  /api/file} : save a new file to the file system.
   *
   * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
   */
  @SneakyThrows
  @PostMapping(value = "/files", consumes = {"multipart/form-data"})
  public ResponseEntity<Void> saveFile(@RequestParam("file") MultipartFile file) {
    log.debug("REST request to post a new file");
    String fileName = fileStorageService.saveFile(file); // todo return id save to persistence -> later on
    URL resourceUrl = fileStorageService.getResourceUrl(fileName);
    return ResponseEntity.created(resourceUrl.toURI()).build();
  }

  /**
   * {@code GET  /api/files} : get a list of all the files uploaded to the file system.
   *
   * @return list of uploaded files
   */
  @GetMapping("/files")
  public ResponseEntity<List<FileArtifact>> listUploadedFiles() {
    return ResponseEntity.ok(fileStorageService.loadAll());
  }

  @GetMapping("/files/{filename}")
  @ResponseBody public ResponseEntity<Resource> getFile(@PathVariable String filename) {

    Resource file = fileStorageService.loadAsResource(filename);
    String downloadFilename = file.getFilename() != null ? file.getFilename()
        : StringUtils.substringBetween(file.getDescription(), "[", "]");
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + downloadFilename + "\"").body(file);
  }

  @DeleteMapping("/files/{filename}")
  public ResponseEntity<Void> deleteFile(@PathVariable String filename) {
    fileStorageService.deleteByFilename(filename);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/files/")
  public ResponseEntity<Void> deleteFiles() {
    fileStorageService.deleteAll();
    return ResponseEntity.noContent().build();
  }

}