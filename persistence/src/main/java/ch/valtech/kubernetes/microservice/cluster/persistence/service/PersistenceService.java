package ch.valtech.kubernetes.microservice.cluster.persistence.service;

import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Message;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.User;
import ch.valtech.kubernetes.microservice.cluster.persistence.dto.AuditingRequestDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.dto.MessageDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.exception.PersistenceException;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.AuditingRepository;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.MessageRepository;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.UserRepository;
import java.time.LocalDate;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PersistenceService {

  private final MessageRepository messageRepository;
  private final AuditingRepository auditingRepository;
  private final UserRepository userRepository;

  public PersistenceService(MessageRepository messageRepository,
      AuditingRepository auditingRepository,
      UserRepository userRepository) {
    this.messageRepository = messageRepository;
    this.auditingRepository = auditingRepository;
    this.userRepository = userRepository;
  }

  public Message getMessageByKey(String key) {
    return messageRepository.findByKeyId(key)
        .orElseThrow(() -> new PersistenceException("Message is not found"));
  }

  public MessageDTO saveNewMessage(AuditingRequestDTO requestDTO) {
    Auditing auditing = addAuditRecord(requestDTO);
    Message message = auditing.getMessage();
    return MessageDTO.builder()
        .key(message.getKeyId())
        .message(message.getValue())
        .build();
  }

  private Auditing addAuditRecord(AuditingRequestDTO requestDTO) {
    Auditing auditing = new Auditing();
    auditing.setUser(getOrCreateUser(requestDTO.getEmail()));
    auditing.setMessage(new Message(requestDTO.getKey(), requestDTO.getMessageValue()));
    auditing.setModificationDate(LocalDate.now());
    auditingRepository.save(auditing);
    return auditing;
  }

  private User getOrCreateUser(String email) {
    return userRepository.findByEmail(email).orElse(new User(email));
  }

}
