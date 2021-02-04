package ch.valtech.kubernetes.microservice.cluster.persistence.service;

import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Message;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.User;
import ch.valtech.kubernetes.microservice.cluster.persistence.dto.AuditingRequestDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.dto.MessageDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.AuditingRepository;
import ch.valtech.kubernetes.microservice.cluster.persistence.repository.MessageRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith({SpringExtension.class})
public class PersistenceServiceTest {
  
  
  private PersistenceService persistenceService;
  
  @Mock
  private AuditingRepository auditingRepository;
  @Mock
  private MessageRepository messageRepository;
  
  private User user;
  
  private MessageMapper fileMapper;
  
  @BeforeEach
  public void setUp() {
    persistenceService = new PersistenceService(messageRepository, auditingRepository);
  }
  
  @Test
  public void shouldGetFileForUserByKey() {
    //given
    user = new User("email.com");
    MessageDTO file = MessageDTO.builder().key("some-key").message("some").build();
    
    Optional<MessageDTO> fileDTO = persistenceService.getMessageByKeyForUser(user, "some-key");
    MessageDTO fileDTO1 = MessageDTO.builder().key(file.getKey()).message(file.getMessage()).build();
    
    assertTrue(fileDTO.isPresent());
    assertEquals(fileDTO1, fileDTO.get());
    
  }
  
  @Test
  public void shouldStoreNewMessage() {
    Auditing auditing = new Auditing();
    auditing.setId(1L);
    auditing.setUser(new User("email1.com"));
    auditing.setMessage(new Message("Ab456", "some message"));
    
    given(auditingRepository.save(auditing)).willReturn(auditing);
    
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
