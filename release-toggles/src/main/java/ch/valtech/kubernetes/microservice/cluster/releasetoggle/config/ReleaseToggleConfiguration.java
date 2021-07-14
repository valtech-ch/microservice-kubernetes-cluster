package ch.valtech.kubernetes.microservice.cluster.releasetoggle.config;

import ch.valtech.kubernetes.microservice.cluster.releasetoggle.aspect.ReleaseToggleAspect;
import io.vavr.collection.Stream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.togglz.core.Feature;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.repository.mem.InMemoryStateRepository;
import org.togglz.core.spi.FeatureProvider;
import org.togglz.core.util.Strings;

@Slf4j
@Configuration
@Profile("release-toggles")
@EnableAspectJAutoProxy
public class ReleaseToggleConfiguration {

  public static final String RELEASE_TOGGLE_BEAN = "releaseToggle";

  private static final boolean RELEASE_TOGGLE_DEFAULT_STATE = Boolean.TRUE;
  private static final String RELEASE_TOGGLE_SEPARATOR = ",";

  @Bean
  public <E extends Feature> FeatureProvider featureProvider(@Qualifier(RELEASE_TOGGLE_BEAN) Class<E> releaseToggles) {
    return new EnumBasedFeatureProvider(releaseToggles);
  }

  @Bean
  public <E extends Feature> StateRepository stateRepository(
      @Value("${togglz.release-toggles-list}") String releaseTogglesList,
      @Qualifier(RELEASE_TOGGLE_BEAN) Class<E> releaseToggle) {
    StateRepository inMemoryStateRepository = new InMemoryStateRepository();
    List<String> releaseToggles = Stream.ofAll(Strings.splitAndTrim(releaseTogglesList, RELEASE_TOGGLE_SEPARATOR))
        .map(rt -> rt.replace("-", "_"))
        .asJava();
    if (releaseToggles.isEmpty()) {
      return inMemoryStateRepository;
    }
    Stream.of(releaseToggle.getEnumConstants())
        .filter(feature -> releaseToggles.contains(feature.name()))
        .forEach(feature -> {
          inMemoryStateRepository.setFeatureState(new FeatureState(feature, RELEASE_TOGGLE_DEFAULT_STATE));
          log.info("Enabled release toggle: {}", feature);
        });
    return inMemoryStateRepository;
  }

  @Bean
  public ReleaseToggleAspect releaseToggleAspect() {
    return new ReleaseToggleAspect();
  }

}
