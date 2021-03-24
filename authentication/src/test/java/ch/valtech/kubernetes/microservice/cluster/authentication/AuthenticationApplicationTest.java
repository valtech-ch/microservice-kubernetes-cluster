package ch.valtech.kubernetes.microservice.cluster.authentication;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.valtech.kubernetes.microservice.cluster.authentication.mock.WithMockOauth2Scope;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MimeTypeUtils;

@AutoConfigureMockMvc
@SpringBootTest
public class AuthenticationApplicationTest {

  private static final String API_URI = "/v2/api-docs";

  @Autowired
  MockMvc mockMvc;

  @Test
  @Disabled
  @WithMockOauth2Scope(scope = "read")
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

}