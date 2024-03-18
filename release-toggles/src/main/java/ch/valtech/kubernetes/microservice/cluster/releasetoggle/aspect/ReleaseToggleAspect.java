package ch.valtech.kubernetes.microservice.cluster.releasetoggle.aspect;

import ch.valtech.kubernetes.microservice.cluster.releasetoggle.annotation.aspect.ReleaseToggle;
import ch.valtech.kubernetes.microservice.cluster.releasetoggle.exception.ReleaseToggleNotEnabledException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.togglz.core.context.FeatureContext;
import org.togglz.core.util.NamedFeature;

@Slf4j
@Aspect
public class ReleaseToggleAspect {

  public ReleaseToggleAspect() {
    log.info("Enabling ReleaseToggleAspect");
  }

  @Around(value = "@within(releaseToggle)")
  public Object checkReleaseToggleWithin(ProceedingJoinPoint joinPoint, ReleaseToggle releaseToggle) throws Throwable {
    return checkToggle(joinPoint, releaseToggle);
  }

  @Around(value = "@annotation(releaseToggle)")
  public Object checkReleaseToggleAnnotation(ProceedingJoinPoint joinPoint, ReleaseToggle releaseToggle) throws Throwable {
    return checkToggle(joinPoint, releaseToggle);
  }

  public Object checkToggle(ProceedingJoinPoint joinPoint, ReleaseToggle releaseToggle) throws Throwable {
    String toggleName = releaseToggle.value();
    boolean isActive = FeatureContext.getFeatureManager().isActive(new NamedFeature(toggleName));
    if (isActive) {
      return joinPoint.proceed();
    }
    log.warn("Release toggle {} is not enabled!", toggleName);
    throw new ReleaseToggleNotEnabledException(toggleName);
  }

}
