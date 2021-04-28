package ch.valtech.kubernetes.microservice.cluster.releasetoggle;

import ch.valtech.kubernetes.microservice.cluster.releasetoggle.annotation.featuregroup.Test;
import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum TestReleaseToggles implements Feature {

  @Test
  @Label("TEST-1: Used in unit/integration tests")
  TEST_1,

  @Test
  @Label("TEST-2: Used in unit/integration tests")
  TEST_2;

  public boolean isActive() {
    return FeatureContext.getFeatureManager().isActive(this);
  }

}
