package ch.valtech.kubernetes.microservice.cluster.releasetoggle.config;

import org.springframework.beans.factory.FactoryBean;
import org.togglz.core.Feature;
import org.togglz.core.manager.FeatureManager;

public class ReleaseToggleBeanFactory<A> implements FactoryBean<A> {

  private final Class<A> beanClass;
  private final A enabled;
  private final A disabled;
  private final Feature releaseToggle;
  private final FeatureManager featureManager;

  public ReleaseToggleBeanFactory(Class<A> beanClass, A enabled, A disabled, Feature releaseToggle,
      FeatureManager featureManager) {
    this.beanClass = beanClass;
    this.enabled = enabled;
    this.disabled = disabled;
    this.releaseToggle = releaseToggle;
    this.featureManager = featureManager;
  }

  @Override
  public A getObject() {
    return featureManager.isActive(releaseToggle) ? enabled : disabled;
  }

  @Override
  public Class<A> getObjectType() {
    return beanClass;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

}
