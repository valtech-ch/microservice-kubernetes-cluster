package ch.valtech.kubernetes.microservice.cluster.filestorage.annotation;

import java.util.Map;
import java.util.stream.Stream;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Profiles;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Implementation of {@link OnProfile} annotation.
 *
 * @see OnProfile
 */
public class OnProfileCondition implements Condition {

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    Map<String, Object> attrs = metadata.getAnnotationAttributes(OnProfile.class.getName());
    if (attrs != null) {
      String[] one = (String[]) attrs.get("any");
      String[] all = (String[]) attrs.get("all");
      boolean matchOne = one.length == 0 || context.getEnvironment().acceptsProfiles(Profiles.of(one));
      boolean matchAll = Stream.of(all)
          .map(value -> context.getEnvironment().acceptsProfiles(Profiles.of(value)))
          .reduce(true, (a, b) -> a && b);
      return matchOne && matchAll;
    } else {
      return true;
    }
  }

}
