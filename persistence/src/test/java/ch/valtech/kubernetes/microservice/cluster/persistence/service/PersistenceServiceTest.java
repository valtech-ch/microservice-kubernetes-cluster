package ch.valtech.kubernetes.microservice.cluster.persistence.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

import ch.valtech.kubernetes.microservice.cluster.common.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.common.dto.AuditingRequestDTO;
import ch.valtech.kubernetes.microservice.cluster.common.dto.MessageDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import ch.valtech.kubernetes.microservice.cluster.persistence.mapper.PersistenceMapper;
import ch.valtech.kubernetes.microservice.cluster.persistence.mapper.PersistenceMapperImpl;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.AuditingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
public class PersistenceServiceTest {
  
  
  private PersistenceService persistenceService;
  
  @Mock
  private AuditingRepository auditingRepository;
  @Mock
  private PersistenceUtils persistenceUtils;

  private PersistenceMapper persistenceMapper = new PersistenceMapperImpl();
  
  
  @BeforeEach
  public void setUp() {
    persistenceService = new PersistenceService(auditingRepository, persistenceMapper);
  }
  
  @Test
  public void shouldStoreNewMessage() {
    Auditing auditing = new Auditing();
    auditing.setId(1L);
    auditing.setUsername("user");
    auditing.setFilename("some message");
    
    given(auditingRepository.save(auditing)).willReturn(auditing);

    MessageDTO message =
        persistenceService.saveNewMessage(AuditingRequestDTO
            .builder()
            .filename("email1.com")
            .action(Action.UPLOAD)
            .build());
    
    assertNotNull(message);
  }
}
