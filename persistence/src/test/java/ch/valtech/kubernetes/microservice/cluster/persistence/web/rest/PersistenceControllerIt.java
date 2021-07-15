package ch.valtech.kubernetes.microservice.cluster.persistence.web.rest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.valtech.kubernetes.microservice.cluster.persistence.AbstractIt;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.AuditingRequestDto;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.MessageDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersistenceControllerIt extends AbstractIt {

  @Autowired
  private ServletWebServerApplicationContext webServerAppCtx;

  @Autowired
  MockMvc mockMvc;

  @Test
  @SneakyThrows
  @WithMockUser(roles = "admin")
  void shouldPostNewMessage() {
    AuditingRequestDto request = AuditingRequestDto.builder()
        .action(Action.UPLOAD)
        .filename("some file")
        .build();
    mockMvc.perform(post("/api/v1/messages")
        .content(convertObjectToJsonBytes(request))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  @WithMockUser(roles = "admin")
  void shouldFailPostNewMessageValidation() {
    AuditingRequestDto request = AuditingRequestDto.builder()
        .action(Action.UPLOAD)
        .build();
    mockMvc.perform(post("/api/v1/messages")
        .content(convertObjectToJsonBytes(request))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  @WithMockUser(roles = "foo")
  void shouldFailPostNewMessagePermissions() {
    AuditingRequestDto request = AuditingRequestDto.builder()
        .action(Action.UPLOAD)
        .filename("some file")
        .build();
    mockMvc.perform(post("/api/v1/messages")
        .content(convertObjectToJsonBytes(request))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  @SneakyThrows
  @WithMockUser(roles = "admin")
  void shouldGetMessages() {
    mockMvc.perform(get("/api/v1/messages"))
        .andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  @WithMockUser(roles = "admin")
  void shouldGetMessagesByFilename() {
    WebTestClient webClient = WebTestClient.bindToServer()
        .baseUrl("http://localhost:" + webServerAppCtx.getWebServer().getPort()).build();

    String testToken = IOUtils.toString(getClass().getResourceAsStream("/test-token"), UTF_8);

    webClient.get()
        .uri("/api/v1/messages/{filename}", "test.txt")
        .header(HttpHeaders.ACCEPT, "application/json")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + testToken)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(MessageDto.class);
  }

  private static String convertObjectToJsonBytes(Object object) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .registerModules(new JavaTimeModule(), new Jdk8Module())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    return objectMapper.writeValueAsString(object);
  }

}
