package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import ch.valtech.kubernetes.microservice.cluster.filestorage.client.ReactivePersistenceClient;
import ch.valtech.kubernetes.microservice.cluster.filestorage.config.JwtRestTemplateConfiguration;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.SearchRequest;
import java.io.IOException;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
class AuditingServiceRestTest {

  private static MockWebServer mockWebServer;

  private static String url;

  private final RestTemplate restTemplate = new JwtRestTemplateConfiguration()
      .jwtRestTemplate(new RestTemplateBuilder());

  private final ReactivePersistenceClient reactivePersistenceClient = new ReactivePersistenceClient(url);

  private final AuditingService auditingService = new AuditingServiceRest(url, restTemplate, reactivePersistenceClient);

  private MockRestServiceServer mockRestServiceServer;

  @BeforeAll
  static void beforeAll() {
    mockWebServer = new MockWebServer();
    url = mockWebServer.url("/api/v1/messages").toString();
  }

  @AfterAll
  static void afterAll() throws IOException {
    mockWebServer.close();
  }

  @BeforeEach
  void mockAuditingCall() {
    mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  void testSuccessfulAudit() {
    mockRestServiceServer.expect(requestTo(url))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess("{\"message\": \"test.txt uploaded successfully\"}", MediaType.APPLICATION_JSON));
    MessageDto response = auditingService.audit("test.txt", Action.UPLOAD);
    assertNotNull(response.getMessage());
    assertEquals("test.txt uploaded successfully", response.getMessage());
  }

  @Test
  void testFailedAudit() {
    mockRestServiceServer.expect(requestTo(url))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withUnauthorizedRequest());
    MessageDto response = auditingService.audit("test.txt", Action.UPLOAD);
    assertNull(response);
  }

  @Test
  void testFailedAuditIoException() {
    mockRestServiceServer.expect(requestTo(url))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withException(new IOException()));
    MessageDto response = auditingService.audit("test.txt", Action.UPLOAD);
    assertNull(response);
  }

  @Test
  void testSearchMessages() {
    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody("{\"message\": \"test.txt uploaded successfully\"}"));

    Flux<MessageDto> search = auditingService
        .search(SearchRequest.newBuilder().setFilename("test.txt").setLimit(2).build());

    List<MessageDto> response = search.collectList().block();
    assertEquals(1, response.size());
    assertEquals("test.txt uploaded successfully", response.get(0).getMessage());
  }

}
