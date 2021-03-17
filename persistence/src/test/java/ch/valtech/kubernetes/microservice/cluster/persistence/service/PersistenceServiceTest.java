package ch.valtech.kubernetes.microservice.cluster.persistence.service;

import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Message;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.User;
import ch.valtech.kubernetes.microservice.cluster.persistence.dto.AuditingRequestDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.dto.MessageDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.AuditingRepository;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.MessageRepository;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith({SpringExtension.class})
public class PersistenceServiceTest {
  
  
  private PersistenceService persistenceService;
  
  @Mock
  private AuditingRepository auditingRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  
  
  @BeforeEach
  public void setUp() {
    persistenceService = new PersistenceService(messageRepository, auditingRepository, userRepository);
  }
  
  @Test
  public void shouldStoreNewMessage() {
    Auditing auditing = new Auditing();
    auditing.setId(1L);
    auditing.setUser(new User("email1.com"));
    auditing.setMessage(new Message("Ab456", "some message"));
    
    given(auditingRepository.save(auditing)).willReturn(auditing);
    given(messageRepository.findByKeyId(anyString())).willReturn(Optional.of(new Message("Ab456", "some message")));
    
    MessageDTO message =
        persistenceService.saveNewMessage(AuditingRequestDTO
            .builder()
            .email("email1.com")
            .key("Ab456")
            .messageValue("some message")
            .build());
    
    assertNotNull(message);
    assertEquals(message.getKey(), "Ab456");
  }
}
