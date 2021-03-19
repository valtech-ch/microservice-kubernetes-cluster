package ch.valtech.kubernetes.microservice.cluster.persistence.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.valtech.kubernetes.microservice.cluster.persistence.dto.AuditingRequestDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class PersistenceControllerTest {
  
  @Autowired
  MockMvc mockMvc;
  
  @Test
  @SneakyThrows
  public void shouldPostNewMessage() {
    AuditingRequestDTO request = AuditingRequestDTO.builder()
        .email("email1.com")
        .key("Ab456")
        .messageValue("some message")
        .build();
    mockMvc.perform(post("/api/v1/messages")
        .content(convertObjectToJsonBytes(request))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  public static String convertObjectToJsonBytes(Object object) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .registerModules(new JavaTimeModule(), new Jdk8Module())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    
    return objectMapper.writeValueAsString(object);
  }
}
