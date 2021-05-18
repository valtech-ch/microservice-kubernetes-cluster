package ch.valtech.kubernetes.microservice.cluster.filestorage.service;

import static java.lang.String.format;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.utility.DockerImageName.parse;

import ch.valtech.kubernetes.microservice.cluster.filestorage.domain.FileArtifact;
import ch.valtech.kubernetes.microservice.cluster.filestorage.exception.FileStorageException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileStorageCloudServiceTest {

  private static final String CONTAINER_NAME = "filestorage";
  private static final String ACCOUNT_NAME = "devstoreaccount1";
  private static final String ACCOUNT_KEY =
      "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";

  @Container
  private static final GenericContainer AZURITE_CONTAINER = new GenericContainer(
      parse("mcr.microsoft.com/azure-storage/azurite"))
      .withExposedPorts(10000);

  private static FileStorageCloudService service;

  private static String baseUrl;

  private final byte[] testFile = toByteArray(getClass().getResourceAsStream("/config/application.yml"));

  FileStorageCloudServiceTest() throws IOException {
  }

  @BeforeAll
  static void setupService() {
    assertThat(AZURITE_CONTAINER.isRunning()).isTrue();

    Integer mappedPort = AZURITE_CONTAINER.getMappedPort(10000);

    baseUrl = format("http://127.0.0.1:%s/%s", mappedPort, ACCOUNT_NAME);

    String connection = new StringBuilder()
        .append("DefaultEndpointsProtocol=http;")
        .append(format("AccountName=%s;", ACCOUNT_NAME))
        .append(format("AccountKey=%s;", ACCOUNT_KEY))
        .append(format("BlobEndpoint=%s", baseUrl))
        .toString();
    service = new FileStorageCloudService(ACCOUNT_NAME, ACCOUNT_KEY, connection,
        CONTAINER_NAME);
  }

  @BeforeEach
  void clear() {
    service.deleteAll();
  }

  @Test
  void testSaveFile() {
    String filename = "test.txt";
    String savedFileName = saveFile(filename);
    assertEquals(filename, savedFileName);

    List<FileArtifact> fileArtifacts = service.loadAll();
    assertEquals(1, fileArtifacts.size());
    assertEquals(filename, fileArtifacts.get(0).getFilename());
  }

  @Test
  void testSaveFileFailed() {
    MockMultipartFile multipartFile = new MockMultipartFile("test.txt", "", null, testFile);
    assertThrows(FileStorageException.class, () -> {
      service.saveFile(multipartFile);
    });
  }

  @Test
  void testGetResourceUrl() {
    String filename = "test.txt";
    saveFile(filename);

    String fileUrl = service.getResourceUrl(filename).toExternalForm();
    assertEquals(baseUrl + "/filestorage/test.txt", fileUrl);
  }

  @Test
  void testLoadAll() {
    List<FileArtifact> fileArtifacts = service.loadAll();
    assertEquals(0, fileArtifacts.size());
  }

  @Test
  @SneakyThrows
  void testLoadAsResource() {
    String filename = "test.txt";
    saveFile(filename);

    Resource resource = service.loadAsResource(filename);
    assertTrue(Arrays.equals(testFile, toByteArray(resource.getInputStream())));
  }

  @Test
  void testDeleteByFilename() {
    String filename = "test.txt";
    assertThrows(ResponseStatusException.class, () -> {
      service.deleteByFilename(filename);
    });

    saveFile(filename);
    assertEquals(1, service.loadAll().size());

    service.deleteByFilename(filename);
    assertEquals(0, service.loadAll().size());
  }

  @Test
  void testDeleteAll() {
    service.deleteAll();

    String filename = "test.txt";
    saveFile(filename);
    assertEquals(1, service.loadAll().size());

    service.deleteAll();
    assertEquals(0, service.loadAll().size());
  }

  @SneakyThrows
  private String saveFile(String filename) {
    MockMultipartFile multipartFile = new MockMultipartFile(filename, filename, null, testFile);
    return service.saveFile(multipartFile);
  }

}
