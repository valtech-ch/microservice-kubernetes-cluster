package ch.valtech.kubernetes.microservice.cluster.filestorage.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.valtech.kubernetes.microservice.cluster.filestorage.config.OauthHelper;
import ch.valtech.kubernetes.microservice.cluster.filestorage.domain.FileArtifact;
import ch.valtech.kubernetes.microservice.cluster.filestorage.exception.FileStorageException;
import ch.valtech.kubernetes.microservice.cluster.filestorage.service.AuditingService;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.dto.Action;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@AutoConfigureMockMvc
@SpringBootTest
class FileStorageControllerIt {

  public static final String FILENAME = "test.txt";
  public static final String ADMIN = "admin";
  private static final String USER = "user";

  @Autowired
  MockMvc mockMvc;
  @Autowired
  OauthHelper helper;
  @MockBean
  private AuditingService auditingService;

  ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @SneakyThrows
  void shouldUploadNewFile() {
    MockMultipartFile file = new MockMultipartFile("file",
        FILENAME,
        MediaType.TEXT_PLAIN_VALUE,
        "test content".getBytes()
    );

    mockMvc.perform(multipart("/api/files").file(file).with(getBearerToken(ADMIN)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost:8080/api/file/" + file.getOriginalFilename()));

    verify(auditingService, times(1)).audit(FILENAME, Action.UPLOAD);
  }

  @Test
  @SneakyThrows
  void shouldFailUploadNewFile() {
    MockMultipartFile file = new MockMultipartFile("file",
        FILENAME,
        MediaType.TEXT_PLAIN_VALUE,
        "test content".getBytes()
    );

    mockMvc.perform(multipart("/api/files").file(file).with(getBearerToken(USER)))
        .andExpect(status().isForbidden());

    verify(auditingService, times(0)).audit(FILENAME, Action.UPLOAD);
  }

  @Test
  @SneakyThrows
  void shouldListUploadedFiles() {
    shouldUploadNewFile();
    MvcResult mvcResult = mockMvc.perform(get("/api/files").with(getBearerToken(USER)))
        .andExpect(status().isOk())
        .andReturn();

    String content = mvcResult.getResponse().getContentAsString();
    List<FileArtifact> files = objectMapper.readValue(content, new TypeReference<List<FileArtifact>>() {
    });
    assertThat(files).contains(FileArtifact.builder().filename(FILENAME).build());
  }

  @Test
  @SneakyThrows
  void shouldGetFileByFilename() {
    shouldUploadNewFile();
    mockMvc.perform(get("/api/files/" + FILENAME).with(getBearerToken(USER)))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Disposition", "attachment; filename=\"" + FILENAME + "\""));

    verify(auditingService, times(1)).audit(FILENAME, Action.DOWNLOAD);
  }

  @Test
  @SneakyThrows
  void shouldFailToGetFileByFilename() {
    mockMvc.perform(delete("/api/files/" + FILENAME).with(getBearerToken(ADMIN)))
        .andExpect(status().isNoContent());

    assertThatThrownBy(() ->
        mockMvc.perform(get("/api/files/" + FILENAME).with(getBearerToken(USER)))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"" + FILENAME + "\"")))
        .hasCause(new FileStorageException("File not found " + FILENAME));

    verify(auditingService, times(0)).audit(FILENAME, Action.DOWNLOAD);
  }

  @Test
  @SneakyThrows
  void shouldDeleteFileByFilename() {
    shouldUploadNewFile();
    mockMvc.perform(delete("/api/files/" + FILENAME).with(getBearerToken(ADMIN)))
        .andExpect(status().isNoContent());

    MvcResult mvcResult = mockMvc.perform(get("/api/files").with(getBearerToken(ADMIN)))
        .andExpect(status().isOk())
        .andReturn();

    String content = mvcResult.getResponse().getContentAsString();
    List<FileArtifact> files = objectMapper.readValue(content, new TypeReference<List<FileArtifact>>() {
    });
    assertThat(files).doesNotContain(FileArtifact.builder().filename(FILENAME).build());
    verify(auditingService, times(1)).audit(FILENAME, Action.DELETE);
  }

  @Test
  @SneakyThrows
  void shouldDeleteAllFiles() {
    shouldUploadNewFile();
    mockMvc.perform(delete("/api/files/").with(getBearerToken(USER)))
        .andExpect(status().isNoContent());

    MvcResult mvcResult = mockMvc.perform(get("/api/files").with(getBearerToken(USER)))
        .andExpect(status().isOk())
        .andReturn();

    String content = mvcResult.getResponse().getContentAsString();
    List<FileArtifact> files = objectMapper.readValue(content, new TypeReference<List<FileArtifact>>() {
    });
    assertThat(files).isEmpty();
    verify(auditingService, times(1)).audit("ALL FILES", Action.DELETE);
  }


  private RequestPostProcessor getBearerToken(String username) {
    return helper.bearerToken("kubernetes-cluster", username);
  }
}