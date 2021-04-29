package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

import static ch.valtech.kubernetes.microservice.cluster.releasetoggle.config.ReleaseToggleConfiguration.RELEASE_TOGGLE_BEAN;

import ch.valtech.kubernetes.microservice.cluster.filestorage.releasetoggle.ReleaseToggles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReleaseTogglesConfiguration {

  @Bean(RELEASE_TOGGLE_BEAN)
  public Class<ReleaseToggles> releaseToggles() {
    return ReleaseToggles.class;
  }

}
