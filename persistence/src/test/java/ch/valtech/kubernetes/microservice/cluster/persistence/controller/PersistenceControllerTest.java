package ch.valtech.kubernetes.microservice.cluster.persistence.controller;

import ch.valtech.kubernetes.microservice.cluster.persistence.dto.AuditingRequestDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.dto.MessageDTO;
import ch.valtech.kubernetes.microservice.cluster.persistence.service.PersistenceService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc()
@SpringBootTest
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
    mockMvc.perform(post("/api/v1/message")
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
