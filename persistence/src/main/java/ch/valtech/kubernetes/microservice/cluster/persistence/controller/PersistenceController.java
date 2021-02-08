package ch.valtech.kubernetes.microservice.cluster.persistence.controller;

import ch.valtech.kubernetes.microservice.cluster.persistence.domain.User;
import ch.valtech.kubernetes.microservice.cluster.persistence.dto.AuditingRequestDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.dto.MessageDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class PersistenceController {
  
  private final PersistenceService persistenceService;
  
  public PersistenceController(PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
    
  }
  
  @PostMapping("/message")
  public ResponseEntity<MessageDTO> saveNewMessage(@RequestBody AuditingRequestDTO requestDTO) {
    MessageDTO newMessage = persistenceService.saveNewMessage(requestDTO);
    
    return ResponseEntity.ok(newMessage);
  }
}
