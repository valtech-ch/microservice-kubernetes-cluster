package ch.valtech.kubernetes.microservice.cluster.persistence.service;

import static ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action.DOWNLOAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import ch.valtech.kubernetes.microservice.cluster.persistence.mapper.PersistenceMapper;
import ch.valtech.kubernetes.microservice.cluster.persistence.mapper.PersistenceMapperImpl;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.AuditingRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
class PersistenceServiceTest {

  private PersistenceService persistenceService;

  @Mock
  private AuditingRepository auditingRepository;

  private final PersistenceMapper persistenceMapper = Mappers.getMapper(PersistenceMapper.class);

  @BeforeEach
  public void setUp() {
    persistenceService = new PersistenceService(auditingRepository, persistenceMapper);
  }

  @Test
  @WithMockUser
  void shouldStoreNewMessage() {
    String filename = "test.txt";
    Auditing auditing = createAuditing(filename);

    given(auditingRepository.save(auditing)).willReturn(Mono.just(auditing));

    MessageDto message =
        persistenceService.saveNewMessage(AuditingRequestDto
            .builder()
            .filename(filename)
            .action(DOWNLOAD)
            .build(), "user").block();

    assertNotNull(message);
    assertEquals("User user downloaded " + filename, message.getMessage());
  }

  @Test
  @WithMockUser
  void getMessages() {
    String filename = "test.txt";
    given(auditingRepository.findBy(any()))
        .willReturn(Flux.just(createAuditing(filename)));

    List<MessageDto> messages = persistenceService.getMessages(10).collectList().block();
    assertEquals(1, messages.size());
    assertEquals("User user downloaded test.txt", messages.get(0).getMessage());
  }

  @Test
  @WithMockUser
  void getMessagesWithFilename() {
    String filename = "test.txt";
    given(auditingRepository.findByFilename(any(), any()))
        .willReturn(Flux.just(createAuditing(filename)));

    List<MessageDto> messages = persistenceService.getMessagesWithFilename(filename, 10).collectList().block();
    assertEquals(1, messages.size());
    assertEquals("User user downloaded test.txt", messages.get(0).getMessage());
  }

  private Auditing createAuditing(String filename) {
    return Auditing.builder()
        .username("user")
        .filename(filename)
        .action(Action.DOWNLOAD)
        .modificationDate(LocalDate.now())
        .build();
  }

}
