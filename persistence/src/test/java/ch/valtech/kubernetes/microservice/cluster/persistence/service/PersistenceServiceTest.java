package ch.valtech.kubernetes.microservice.cluster.persistence.service;

import static ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action.DOWNLOAD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class PersistenceServiceTest {

  private PersistenceService persistenceService;

  @Mock
  private AuditingRepository auditingRepository;

  private final PersistenceMapper persistenceMapper = new PersistenceMapperImpl();

  @BeforeEach
  public void setUp() {
    persistenceService = new PersistenceService(auditingRepository, persistenceMapper);
  }

  @Test
  @WithMockUser
  void shouldStoreNewMessage() {
    String filename = "test.txt";
    LocalDate modificationDate = LocalDate.now();
    Auditing auditing = new Auditing();
    auditing.setUsername("user");
    auditing.setFilename(filename);
    auditing.setAction(Action.DOWNLOAD);
    auditing.setModificationDate(modificationDate);

    given(auditingRepository.save(auditing)).willReturn(auditing);

    MessageDto message =
        persistenceService.saveNewMessage(AuditingRequestDto
            .builder()
            .filename(filename)
            .action(DOWNLOAD)
            .build());

    assertNotNull(message);
    assertEquals("User user downloaded " + filename, message.getMessage());
  }

  @Test
  @WithMockUser
  void getMessages() {
    String filename = "test.txt";
    List<MessageDto> messages = persistenceService.getMessages(10);
    assertEquals(0, messages.size());
  }

}
