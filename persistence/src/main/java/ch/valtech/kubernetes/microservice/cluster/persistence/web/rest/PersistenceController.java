package ch.valtech.kubernetes.microservice.cluster.persistence.web.rest;

import static ch.valtech.kubernetes.microservice.cluster.persistence.util.SecurityUtils.getUsername;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class PersistenceController {

  private final PersistenceService persistenceService;

  public PersistenceController(PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
  }

  @GetMapping("/messages")
  @PreAuthorize("hasAnyRole('admin')")
  public ResponseEntity<List<MessageDto>> getMessages(@RequestParam(name = "limit", defaultValue = "10") int limit) {
    log.info("Get last {} messages", limit);
    return ResponseEntity.ok(persistenceService.getMessages(limit).collectList().block());
  }

  @GetMapping("/messages/{filename}")
  @PreAuthorize("hasAnyRole('admin', 'user')")
  public Flux<MessageDto> getMessagesByFilename(
      @PathVariable String filename,
      @RequestParam(name = "limit", defaultValue = "10") int limit) {
    log.info("Get last {} messages of file {}", limit, filename);
    return persistenceService.getMessagesWithFilename(filename, limit);
  }

  @PostMapping("/messages")
  @PreAuthorize("hasAnyRole('admin', 'user')")
  public ResponseEntity<MessageDto> saveNewMessage(@Valid @RequestBody AuditingRequestDto requestDto) {
    String username = getUsername().orElseThrow(() ->
        new ResponseStatusException(FORBIDDEN, "Username not found"));
    MessageDto newMessage = persistenceService.saveNewMessage(requestDto, username).block();
    log.info("** New message was just posted! ");
    return ResponseEntity.ok(newMessage);
  }

}
