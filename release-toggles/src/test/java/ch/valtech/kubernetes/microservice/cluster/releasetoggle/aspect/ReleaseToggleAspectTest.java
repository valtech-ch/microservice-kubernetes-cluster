package ch.valtech.kubernetes.microservice.cluster.releasetoggle.aspect;

import static ch.valtech.kubernetes.microservice.cluster.releasetoggle.config.ReleaseToggleConfiguration.RELEASE_TOGGLE_BEAN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import ch.valtech.kubernetes.microservice.cluster.releasetoggle.TestReleaseToggles;
import ch.valtech.kubernetes.microservice.cluster.releasetoggle.annotation.aspect.ReleaseToggle;
import ch.valtech.kubernetes.microservice.cluster.releasetoggle.config.ReleaseToggleConfiguration;
import ch.valtech.kubernetes.microservice.cluster.releasetoggle.exception.ReleaseToggleNotEnabledException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.togglz.core.context.FeatureContext;
import org.togglz.core.repository.FeatureState;
import org.togglz.junit5.AllDisabled;
import org.togglz.junit5.AllEnabled;
import org.togglz.spring.boot.actuate.autoconfigure.TogglzAutoConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    TogglzAutoConfiguration.class,
    ReleaseToggleAspectTest.class,
    ReleaseToggleConfiguration.class})
@ActiveProfiles("release-toggles")
@DirtiesContext
public class ReleaseToggleAspectTest {

  @Autowired
  private TestServiceImpl testServiceImpl;

  @Test
  @AllEnabled(TestReleaseToggles.class)
  void aspectProceedsToTestMethodWhenFeatureToggleEnabled() {
    FeatureContext.getFeatureManager().setFeatureState(new FeatureState(TestReleaseToggles.TEST_2, true));
    assertThat(testServiceImpl.testMethod(), is("Aspect works!"));
  }

  @Test
  @AllDisabled(TestReleaseToggles.class)
  void aspectThrowsExceptionWhenFeatureToggleDisabled() {
    FeatureContext.getFeatureManager().setFeatureState(new FeatureState(TestReleaseToggles.TEST_2, false));
    Assertions.assertThrows(ReleaseToggleNotEnabledException.class, () -> {
      testServiceImpl.testMethod();
    });
  }

  @Bean(RELEASE_TOGGLE_BEAN)
  public Class<TestReleaseToggles> releaseToggle() {
    return TestReleaseToggles.class;
  }

  @Bean
  public TestServiceImpl testService() {
    return new TestServiceImpl();
  }

  static class TestServiceImpl {

    @ReleaseToggle("TEST_2")
    public String testMethod() {
      return "Aspect works!";
    }

  }

}
