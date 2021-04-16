package ch.valtech.kubernetes.microservice.cluster.persistence.web.rest;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class PersistenceController {

  private final PersistenceService persistenceService;

  public PersistenceController(PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
  }

  @PostMapping("/messages")
  @PreAuthorize("hasAnyRole('admin', 'user')")
  public ResponseEntity<MessageDto> saveNewMessage(@RequestBody AuditingRequestDto requestDto) {
    MessageDto newMessage = persistenceService.saveNewMessage(requestDto);
    log.info("** New message was just posted! ");
    return ResponseEntity.ok(newMessage);
  }

}
