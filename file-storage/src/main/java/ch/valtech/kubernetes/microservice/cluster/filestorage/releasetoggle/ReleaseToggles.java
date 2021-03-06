package ch.valtech.kubernetes.microservice.cluster.filestorage.releasetoggle;

import ch.valtech.kubernetes.microservice.cluster.releasetoggle.annotation.featuregroup.Test;
import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum ReleaseToggles implements Feature {

  @Test
  @Label("VJAP-24: Test")
  VJAP_24;

  public boolean isActive() {
    return FeatureContext.getFeatureManager().isActive(this);
  }

}
