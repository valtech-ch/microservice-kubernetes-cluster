package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@ExtendWith({SpringExtension.class})
class FunctionsServiceTest {

  private final String url = "https://vtch-functions.azurewebsites.net/api";
  private final String key = "123456";

  private FunctionsService functionsService;
  private MockRestServiceServer mockRestServiceServer;

  @BeforeEach
  void mockAuditingCall() {
    functionsService = new FunctionsService(url, key, new RestTemplateBuilder());
    RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(functionsService, "restTemplate");
    mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  void testSuccessfulEcho() {
    mockRestServiceServer.expect(requestTo(url + "/echo"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().string("test"))
        .andExpect(header(FunctionsService.FUNCTION_KEY_HEADER, key))
        .andRespond(withSuccess("test", MediaType.TEXT_PLAIN));
    String response = functionsService.echo("test");
    assertNotNull(response);
    assertEquals("test", response);
  }

  @Test
  void testSuccessfulReverse() {
    mockRestServiceServer.expect(requestTo(url + "/reverse"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().string("test"))
        .andExpect(header(FunctionsService.FUNCTION_KEY_HEADER, key))
        .andRespond(withSuccess("tset", MediaType.TEXT_PLAIN));
    String response = functionsService.reverse("test");
    assertNotNull(response);
    assertEquals("tset", response);
  }

}
