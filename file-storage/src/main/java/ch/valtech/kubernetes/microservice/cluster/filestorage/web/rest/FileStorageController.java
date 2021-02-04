package ch.valtech.kubernetes.microservice.cluster.filestorage.web.rest;

import ch.valtech.kubernetes.microservice.cluster.filestorage.domain.FileArtifact;
import ch.valtech.kubernetes.microservice.cluster.filestorage.service.FileStorageService;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

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
    fileStorageService.saveFile(file); // todo return id save to persistence -> later on
    return ResponseEntity.ok().build();
//    return ResponseEntity.created(URI.create("endpoint url for getting")).build();// todo location header
  }

  /**
   * {@code GET  /api/files} : get a list of all the files uploaded to the file system
   * @return list of uploaded files
   */
  @GetMapping("/files")
  public ResponseEntity<List<FileArtifact>> listUploadedFiles() {
    return ResponseEntity.ok(fileStorageService.loadAll());
  }

  @GetMapping("/file/{filename}")
  @ResponseBody public ResponseEntity<Resource> getFile(@PathVariable String filename) {

    Resource file = fileStorageService.loadAsResource(filename);
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }

  @DeleteMapping("/file/{filename}")
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