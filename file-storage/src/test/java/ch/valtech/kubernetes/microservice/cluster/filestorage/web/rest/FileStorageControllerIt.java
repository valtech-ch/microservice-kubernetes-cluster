package ch.valtech.kubernetes.microservice.cluster.filestorage.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.valtech.kubernetes.microservice.cluster.filestorage.domain.FileArtifact;
import ch.valtech.kubernetes.microservice.cluster.filestorage.service.AuditingService;
import ch.valtech.kubernetes.microservice.cluster.filestorage.service.FileStorageService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class FileStorageControllerIt {

  public static final String FILENAME = "test.txt";
  private FileStorageService fileStorageService;
  @Autowired
  MockMvc mockMvc;
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

    mockMvc.perform(multipart("/api/files").file(file))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost:8080/api/file/" + file.getOriginalFilename()));

    verify(auditingService, times(1)).audit(FILENAME, Action.UPLOAD);
  }

  @Test
  @SneakyThrows
  void shouldListUploadedFiles() {
    shouldUploadNewFile();
    MvcResult mvcResult = mockMvc.perform(get("/api/files"))
        .andExpect(status().isOk())
        .andReturn();

    String content = mvcResult.getResponse().getContentAsString();
    List<FileArtifact> files = objectMapper.readValue(content, new TypeReference<List<FileArtifact>>() {});
    assertThat(files).contains(FileArtifact.builder().filename(FILENAME).build());
  }

  @Test
  @SneakyThrows
  void shouldGetFileByFilename() {
    shouldUploadNewFile();
    mockMvc.perform(get("/api/files/" + FILENAME))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Disposition", "attachment; filename=\"" + FILENAME + "\""));

    verify(auditingService, times(1)).audit(FILENAME, Action.DOWNLOAD);
  }

  @Test
  @SneakyThrows
  void shouldDeleteFileByFilename() {
    shouldUploadNewFile();
    mockMvc.perform(delete("/api/files/" + FILENAME))
        .andExpect(status().isNoContent());

    MvcResult mvcResult = mockMvc.perform(get("/api/files"))
        .andExpect(status().isOk())
        .andReturn();

    String content = mvcResult.getResponse().getContentAsString();
    List<FileArtifact> files = objectMapper.readValue(content, new TypeReference<List<FileArtifact>>() {});
    assertThat(files).doesNotContain(FileArtifact.builder().filename(FILENAME).build());
    verify(auditingService, times(1)).audit(FILENAME, Action.DELETE);
  }

  @Test
  @SneakyThrows
  void shouldDeleteAllFiles() {
    shouldUploadNewFile();
    mockMvc.perform(delete("/api/files/"))
        .andExpect(status().isNoContent());

    MvcResult mvcResult = mockMvc.perform(get("/api/files"))
        .andExpect(status().isOk())
        .andReturn();

    String content = mvcResult.getResponse().getContentAsString();
    List<FileArtifact> files = objectMapper.readValue(content, new TypeReference<List<FileArtifact>>() {});
    assertThat(files).isEmpty();
    verify(auditingService, times(1)).audit("ALL FILES", Action.DELETE);
  }

}