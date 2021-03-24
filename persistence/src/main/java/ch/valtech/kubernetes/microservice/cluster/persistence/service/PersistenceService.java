package ch.valtech.kubernetes.microservice.cluster.persistence.service;

import static ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceUtils.createMessage;
import static ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceUtils.getUsername;

import ch.valtech.kubernetes.microservice.cluster.common.dto.AuditingRequestDTO;
import ch.valtech.kubernetes.microservice.cluster.common.dto.MessageDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import ch.valtech.kubernetes.microservice.cluster.persistence.mapper.PersistenceMapper;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.AuditingRepository;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

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

  public MessageDTO saveNewMessage(AuditingRequestDTO requestDTO) {
    Auditing auditing = addAuditRecord(requestDTO);
    return MessageDTO.builder()
        .message(createMessage(auditing))
        .build();
  }

  private Auditing addAuditRecord(AuditingRequestDTO requestDTO) {
    return auditingRepository.save(
        persistenceMapper.toAuditing(requestDTO, getUsername().get()));
  }
}
