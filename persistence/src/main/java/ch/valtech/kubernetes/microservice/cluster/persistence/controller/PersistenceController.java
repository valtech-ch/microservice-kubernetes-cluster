package ch.valtech.kubernetes.microservice.cluster.persistence.controller;

import ch.valtech.kubernetes.microservice.cluster.persistence.dto.AuditingRequestDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.dto.MessageDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1")
public class PersistenceController {

  private final PersistenceService persistenceService;
  private static final Logger LOG = Logger.getLogger(PersistenceController.class.getName());
  
  public PersistenceController(PersistenceService persistenceService) {
    this.persistenceService = persistenceService;
  }

  @PostMapping("/messages")
  public ResponseEntity<MessageDTO> saveNewMessage(@RequestBody AuditingRequestDTO requestDTO) {
    MessageDTO newMessage = persistenceService.saveNewMessage(requestDTO);
    LOG.log(Level.INFO, "** New message was just posted! ");
    return ResponseEntity.ok(newMessage);
  }

}
