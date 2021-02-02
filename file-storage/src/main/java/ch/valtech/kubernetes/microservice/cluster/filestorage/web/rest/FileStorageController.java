package ch.valtech.kubernetes.microservice.cluster.filestorage.web.rest;

import ch.valtech.kubernetes.microservice.cluster.filestorage.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
  @PostMapping(value = "/file", consumes = {"multipart/form-data"})
  public ResponseEntity<Void> saveFile(@RequestParam("file") MultipartFile file) {
    log.debug("REST request to post a new file");
    fileStorageService.saveFile(file);
    return ResponseEntity.ok().build();
  }

}