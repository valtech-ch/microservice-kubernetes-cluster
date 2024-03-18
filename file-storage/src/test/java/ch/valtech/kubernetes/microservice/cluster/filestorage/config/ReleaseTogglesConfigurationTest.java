package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

import static ch.valtech.kubernetes.microservice.cluster.releasetoggle.config.ReleaseToggleConfiguration.RELEASE_TOGGLE_BEAN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

import ch.valtech.kubernetes.microservice.cluster.filestorage.releasetoggle.ReleaseToggles;
import ch.valtech.kubernetes.microservice.cluster.releasetoggle.config.ReleaseToggleConfiguration;
import java.util.Set;
import org.hamcrest.CoreMatchers;
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

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    TogglzAutoConfiguration.class,
    ReleaseToggleConfiguration.class,
    ReleaseTogglesConfigurationTest.class})
@DirtiesContext
@ActiveProfiles(SpringProfiles.RELEASE_TOGGLES)
class ReleaseTogglesConfigurationTest {

  @Autowired
  private FeatureManager featureManager;

  @Test
  void allReleaseTogglesShouldHaveLabel() {
    final Set<Feature> releaseToggles = featureManager.getFeatures();
    assertThat(releaseToggles, CoreMatchers.hasItems(ReleaseToggles.values()));
    releaseToggles.forEach(rt -> {
      final String label = featureManager.getMetaData(rt).getLabel();
      assertThat(label, Matchers.containsString(":"));
    });
  }

  @Bean(RELEASE_TOGGLE_BEAN)
  public Class<ReleaseToggles> releaseToggles() {
    return ReleaseToggles.class;
  }

}
