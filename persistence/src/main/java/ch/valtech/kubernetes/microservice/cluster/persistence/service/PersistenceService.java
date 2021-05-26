package ch.valtech.kubernetes.microservice.cluster.persistence.service;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.mapper.PersistenceMapper;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.AuditingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class PersistenceService {

  private final AuditingRepository auditingRepository;
  private final PersistenceMapper persistenceMapper;

  public PersistenceService(AuditingRepository auditingRepository,
      PersistenceMapper persistenceMapper) {
    this.auditingRepository = auditingRepository;
    this.persistenceMapper = persistenceMapper;
  }

  @Transactional
  public Mono<MessageDto> saveNewMessage(AuditingRequestDto requestDto, String username) {
    return auditingRepository.save(persistenceMapper.toAuditing(requestDto, username))
        .map(PersistenceUtils::createMessage)
        .map(message -> MessageDto.builder().message(message).build());
  }

  public Flux<MessageDto> getMessages(int limit) {
    Pageable pageRequest = PageRequest.of(0, limit, Direction.DESC, "id");
    return auditingRepository.findBy(pageRequest)
        .map(PersistenceUtils::createMessage)
        .map(message -> MessageDto.builder().message(message).build());
  }

  public Flux<MessageDto> getMessagesWithFilename(String filename, int limit) {
    Pageable pageRequest = PageRequest.of(0, limit, Direction.DESC, "id");
    return auditingRepository.findByFilename(filename, pageRequest)
        .map(PersistenceUtils::createMessage)
        .map(message -> MessageDto.builder().message(message).build());
  }

}
