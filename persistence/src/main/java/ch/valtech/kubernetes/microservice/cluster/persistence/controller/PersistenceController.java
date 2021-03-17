package ch.valtech.kubernetes.microservice.cluster.persistence.controller;

import ch.valtech.kubernetes.microservice.cluster.persistence.dto.AuditingRequestDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.dto.MessageDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PersistenceController {

  private final PersistenceService persistenceService;

  public PersistenceController(PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
  }

  @PostMapping("/messages")
  public ResponseEntity<MessageDTO> saveNewMessage(@RequestBody AuditingRequestDTO requestDTO) {
    MessageDTO newMessage = persistenceService.saveNewMessage(requestDTO);
    return ResponseEntity.ok(newMessage);
  }

}
