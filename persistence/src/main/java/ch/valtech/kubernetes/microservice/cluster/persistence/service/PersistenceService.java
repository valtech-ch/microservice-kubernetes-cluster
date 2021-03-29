package ch.valtech.kubernetes.microservice.cluster.persistence.service;

import static ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceUtils.createMessage;
import static ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceUtils.getUsername;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import ch.valtech.kubernetes.microservice.cluster.persistence.exception.PersistenceException;
import ch.valtech.kubernetes.microservice.cluster.persistence.mapper.PersistenceMapper;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.AuditingRepository;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
public class PersistenceService {

  private final AuditingRepository auditingRepository;
  private final PersistenceMapper persistenceMapper;

  public PersistenceService(AuditingRepository auditingRepository,
      PersistenceMapper persistenceMapper) {
    this.auditingRepository = auditingRepository;
    this.persistenceMapper = persistenceMapper;
  }

  public MessageDto saveNewMessage(AuditingRequestDto requestDto) {
    Auditing auditing = addAuditRecord(requestDto);
    String message = createMessage(auditing);
    log.info(message);
    return MessageDto.builder()
        .message(message)
        .build();
  }

  private Auditing addAuditRecord(AuditingRequestDto requestDto) {
    String username = getUsername().orElseThrow(() -> new PersistenceException("Username not found"));
    return auditingRepository.save(
        persistenceMapper.toAuditing(requestDto, username));
  }
}
