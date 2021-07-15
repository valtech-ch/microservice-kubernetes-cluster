package ch.valtech.kubernetes.microservice.cluster.filestorage.web.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import ch.valtech.kubernetes.microservice.cluster.filestorage.domain.FileArtifact;
import ch.valtech.kubernetes.microservice.cluster.filestorage.kafka.ProducerService;
import ch.valtech.kubernetes.microservice.cluster.filestorage.service.AuditingService;
import ch.valtech.kubernetes.microservice.cluster.filestorage.service.FileStorageService;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.SearchRequest;
import java.net.URL;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

/**
 * REST controller for managing Files.
 */
@Slf4j
@Controller
@RequestMapping("/api")
public class FileStorageController {

  public static final String ALL_FILES = "ALL FILES";

  private final FileStorageService fileStorageService;
  private final AuditingService auditingServiceRest;
  private final AuditingService auditingServiceGrpc;
  private final ProducerService producerService;

  public FileStorageController(@Qualifier("cacheable") FileStorageService fileStorageService,
      @Qualifier("auditingServiceRest") AuditingService auditingServiceRest,
      @Qualifier("auditingServiceGrpc") AuditingService auditingServiceGrpc,
      ProducerService producerService) {
    this.fileStorageService = fileStorageService;
    this.auditingServiceRest = auditingServiceRest;
    this.auditingServiceGrpc = auditingServiceGrpc;
    this.producerService = producerService;
  }

  /**
   * {@code POST  /api/file} : save a new file to the file system.
   *
   * @return the {@link ResponseEntity} with status {@code 200 (OK)}.
   */
  @SneakyThrows
  @PostMapping(value = "/files", consumes = {"multipart/form-data"})
  @PreAuthorize("hasAnyRole('admin')")
  public ResponseEntity<Void> saveFile(@RequestParam("file") MultipartFile file) {
    log.debug("REST request to post a new file");
    if (file.isEmpty()) {
      throw new ResponseStatusException(BAD_REQUEST, "File must not be empty");
    }
    String fileName = fileStorageService.saveFile(file);
    producerService.sendMessage(fileName, Action.UPLOAD);
    URL resourceUrl = fileStorageService.getResourceUrl(fileName);
    return ResponseEntity.created(resourceUrl.toURI()).build();
  }

  /**
   * {@code GET  /api/files} : get a list of all the files uploaded to the file system.
   *
   * @return list of uploaded files
   */
  @GetMapping("/files")
  @PreAuthorize("hasAnyRole('admin', 'user')")
  public ResponseEntity<List<FileArtifact>> listUploadedFiles() {
    return ResponseEntity.ok(fileStorageService.loadAll());
  }

  /**
   * {@code GET  /api/files/changes} : get a list of all the changes done to a file using Flux.
   *
   * @return stream of MessageDto
   */
  @GetMapping("/files/changes/{filename}")
  @PreAuthorize("hasAnyRole('admin', 'user')")
  public Flux<MessageDto> searchFileChanges(@PathVariable String filename,
      @RequestParam(name = "limit", defaultValue = "10") int limit) {
    return auditingServiceRest.search(SearchRequest.newBuilder()
        .setFilename(filename)
        .setLimit(limit)
        .build());
  }

  @GetMapping("/files/{filename}")
  @ResponseBody
  @PreAuthorize("hasAnyRole('admin', 'user')")
  public ResponseEntity<Resource> getFile(@PathVariable String filename) {
    Resource file = fileStorageService.loadAsResource(filename);
    auditingServiceGrpc.audit(filename, Action.DOWNLOAD);
    String downloadFilename = file.getFilename() != null ? file.getFilename()
        : StringUtils.substringBetween(file.getDescription(), "[", "]");
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + downloadFilename + "\"").body(file);
  }

  @DeleteMapping("/files/{filename}")
  @PreAuthorize("hasAnyRole('admin')")
  public ResponseEntity<Void> deleteFile(@PathVariable String filename) {
    fileStorageService.deleteByFilename(filename);
    auditingServiceRest.audit(filename, Action.DELETE);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/files/")
  @PreAuthorize("hasAnyRole('admin')")
  public ResponseEntity<Void> deleteFiles() {
    fileStorageService.deleteAll();
    auditingServiceRest.audit(ALL_FILES, Action.DELETE);
    return ResponseEntity.noContent().build();
  }

}
