package ch.valtech.kubernetes.microservice.cluster.persistence.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.valtech.kubernetes.microservice.cluster.persistence.dto.AuditingRequestDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MimeTypeUtils;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class PersistenceControllerTest {

  private static final String API_URI = "/v2/api-docs";

  @Autowired
  MockMvc mockMvc;

  @Test
  @SneakyThrows
  void shouldPostNewMessage() {
    AuditingRequestDto request = AuditingRequestDto.builder()
        .email("email1.com")
        .key("Ab456")
        .messageValue("some message")
        .build();
    mockMvc.perform(post("/api/v1/messages")
        .content(convertObjectToJsonBytes(request))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void createSpringfoxSwaggerJson() throws Exception {
    String outputDir = System.getProperty("io.springfox.staticdocs.outputDir");
    MvcResult mvcResult = mockMvc.perform(get(API_URI)
        .accept(MimeTypeUtils.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andReturn();

    String swaggerJson = mvcResult.getResponse().getContentAsString();
    Files.createDirectories(Paths.get(outputDir));
    try (BufferedWriter writer = Files
        .newBufferedWriter(Paths.get(outputDir, "swagger.json"), StandardCharsets.UTF_8)) {
      writer.write(swaggerJson);
    }
  }

  private static String convertObjectToJsonBytes(Object object) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .registerModules(new JavaTimeModule(), new Jdk8Module())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    return objectMapper.writeValueAsString(object);
  }
}
