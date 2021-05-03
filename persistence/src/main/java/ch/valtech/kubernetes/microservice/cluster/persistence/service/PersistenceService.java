package ch.valtech.kubernetes.microservice.cluster.persistence.service;

import static ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceUtils.createMessage;
import static ch.valtech.kubernetes.microservice.cluster.persistence.util.SecurityUtils.getUsername;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import ch.valtech.kubernetes.microservice.cluster.persistence.mapper.PersistenceMapper;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.AuditingRepository;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

  public List<MessageDto> getMessages(int limit) {
    Pageable pageRequest = PageRequest.of(0, limit, Direction.DESC, "id");
    return Stream.ofAll(Option.of(auditingRepository.findAll(pageRequest)).getOrElse(Page.empty()))
        .map(PersistenceUtils::createMessage)
        .map(message -> MessageDto.builder().message(message).build())
        .asJava();
  }

  private Auditing addAuditRecord(AuditingRequestDto requestDto) {
    String username = getUsername().orElseThrow(() ->
        new ResponseStatusException(FORBIDDEN, "Username not found"));
    return auditingRepository.save(
        persistenceMapper.toAuditing(requestDto, username));
  }

}
