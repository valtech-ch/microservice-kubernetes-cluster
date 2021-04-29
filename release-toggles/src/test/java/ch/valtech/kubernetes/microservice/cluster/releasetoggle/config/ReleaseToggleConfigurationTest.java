package ch.valtech.kubernetes.microservice.cluster.releasetoggle.config;

import static ch.valtech.kubernetes.microservice.cluster.releasetoggle.config.ReleaseToggleConfiguration.RELEASE_TOGGLE_BEAN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

import ch.valtech.kubernetes.microservice.cluster.releasetoggle.TestReleaseToggles;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;
import org.togglz.spring.boot.actuate.autoconfigure.TogglzAutoConfiguration;

@ActiveProfiles("release-toggles")
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(classes = {
    TogglzAutoConfiguration.class,
    ReleaseToggleConfiguration.class,
    ReleaseToggleConfigurationTest.class})
public class ReleaseToggleConfigurationTest {

  @Autowired
  private FeatureManager featureManager;

  @Test
  void allReleaseTogglesShouldHaveLabel() {
    final Set<Feature> releaseToggles = featureManager.getFeatures();
    assertThat(releaseToggles, hasItems(TestReleaseToggles.values()));
    releaseToggles.forEach(rt -> {
      String label = featureManager.getMetaData(rt).getLabel();
      assertThat("All release toggle enum values should be annotated with @Label(\"TEST-123: Description\")",
          label, Matchers.containsString(":"));
    });
  }

  @Bean(RELEASE_TOGGLE_BEAN)
  public Class<TestReleaseToggles> releaseToggle() {
    return TestReleaseToggles.class;
  }

}
