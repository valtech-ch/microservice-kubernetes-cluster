package ch.valtech.kubernetes.microservice.cluster.persistence.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.valtech.kubernetes.microservice.cluster.persistence.AbstractIt;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MimeTypeUtils;

@Disabled
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersistenceControllerTest extends AbstractIt {

  private static final String API_URI = "/v2/api-docs";

  @Autowired
  MockMvc mockMvc;

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

}
