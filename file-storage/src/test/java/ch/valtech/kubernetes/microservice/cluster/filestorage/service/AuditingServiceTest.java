package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@ExtendWith({SpringExtension.class})
class AuditingServiceTest {

  private final RestTemplate restTemplate = new RestTemplate();
  private final String url = "http://localhost:8081/api/v1/messages";

  private AuditingService auditingService;
  private MockRestServiceServer mockRestServiceServer;

  @BeforeEach
  void mockAuditingCall() {
    auditingService = new AuditingService(url, restTemplate);
    mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  public void testSuccessfulAudit() {
    mockRestServiceServer.expect(requestTo(url))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess("{\"message\": \"test.txt uploaded successfully\"}", MediaType.APPLICATION_JSON));
    MessageDto response = auditingService.audit("test.txt", Action.UPLOAD);
    assertNotNull(response.getMessage());
    assertEquals("test.txt uploaded successfully", response.getMessage());
  }

  @Test
  public void testFailedAudit() {
    mockRestServiceServer.expect(requestTo(url))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withUnauthorizedRequest());
    MessageDto response = auditingService.audit("test.txt", Action.UPLOAD);
    assertNull(response);
  }

  @Test
  public void testFailedAuditIoException() {
    mockRestServiceServer.expect(requestTo(url))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withException(new IOException()));
    MessageDto response = auditingService.audit("test.txt", Action.UPLOAD);
    assertNull(response);
  }
}