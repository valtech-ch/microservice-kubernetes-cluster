package ch.valtech.kubernetes.microservice.cluster.filestorage.web.rest.example;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.valtech.kubernetes.microservice.cluster.filestorage.config.example.ExampleReleaseToggles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.togglz.junit5.AllDisabled;
import org.togglz.junit5.AllEnabled;

@ActiveProfiles({"test", "release-toggles"})
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
@EmbeddedKafka
@AutoConfigureMockMvc
class ExampleReleaseTogglesControllerIt {

  private static final String STATEMENT_PATH = "/example/statement";
  private static final String ASPECT_PATH = "/example/aspect";

  @Autowired
  private MockMvc mockMvc;

  @Test
  @AllDisabled(ExampleReleaseToggles.class)
  void returnsHttp404WhenReleaseToggleDisabledWithIfStatement() throws Exception {
    mockMvc.perform(get(STATEMENT_PATH)).andExpect(status().isNotFound());
  }

  @Test
  @AllDisabled(ExampleReleaseToggles.class)
  void returnsHttp404WhenReleaseToggleDisabledWithIfAspect() throws Exception {
    mockMvc.perform(get(ASPECT_PATH)).andExpect(status().isNotFound());
  }

  @Test
  @AllEnabled(ExampleReleaseToggles.class)
  void returnsHttp200WhenReleaseToggleEnabledWithIfStatement() throws Exception {
    mockMvc.perform(get(STATEMENT_PATH)).andExpect(status().isOk())
        .andExpect(content().string("TEST_123 is enabled."));
  }

  @Test
  @AllEnabled(ExampleReleaseToggles.class)
  void returnsHttp200WhenReleaseToggleEnabledWithIfAspect() throws Exception {
    mockMvc.perform(get(ASPECT_PATH)).andExpect(status().isOk())
        .andExpect(content().string("Example works!"));
  }

}
