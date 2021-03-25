package ch.valtech.kubernetes.microservice.cluster.persistence.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import ch.valtech.kubernetes.microservice.cluster.persistence.mapper.PersistenceMapper;
import ch.valtech.kubernetes.microservice.cluster.persistence.mapper.PersistenceMapperImpl;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.AuditingRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
public class PersistenceServiceTest {

  private PersistenceService persistenceService;

  @Mock
  private AuditingRepository auditingRepository;

  private PersistenceMapper persistenceMapper = new PersistenceMapperImpl();


  @BeforeEach
  public void setUp() {
    persistenceService = new PersistenceService(auditingRepository, persistenceMapper);
  }

  @Test
  @WithMockUser
  public void shouldStoreNewMessage() {
    String filename = "test.txt";
    LocalDate modificationDate = LocalDate.now();
    Auditing auditing = new Auditing();
    auditing.setUsername("user");
    auditing.setFilename(filename);
    auditing.setAction(ch.valtech.kubernetes.microservice.cluster.persistence.domain.Action.UPLOAD);
    auditing.setModificationDate(modificationDate);

    given(auditingRepository.save(auditing)).willReturn(auditing);

    MessageDto message =
        persistenceService.saveNewMessage(AuditingRequestDto
            .builder()
            .filename(filename)
            .action(Action.UPLOAD)
            .build());

    assertNotNull(message);
    assertEquals("User user uploaded " + filename, message.getMessage());
  }
}
