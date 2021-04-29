package ch.valtech.kubernetes.microservice.cluster.filestorage.config.example;

import ch.valtech.kubernetes.microservice.cluster.releasetoggle.annotation.featuregroup.Test;
import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum ExampleReleaseToggles implements Feature {

  @Test
  @Label("TEST-123: Used only in unit/integration tests")
  TEST_123;

  public boolean isActive() {
    return FeatureContext.getFeatureManager().isActive(this);
  }

}