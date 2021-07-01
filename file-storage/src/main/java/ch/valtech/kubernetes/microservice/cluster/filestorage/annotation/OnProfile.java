package ch.valtech.kubernetes.microservice.cluster.filestorage.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnProfileCondition.class)
public @interface OnProfile {

  /**
   * All specified profiles have to be active in order to register the bean.
   */
  String[] all() default {};

  /**
   * Any of the specified profiles has to be active in order to register the bean.
   *
   * @see #value()
   */
  @AliasFor("value")
  String[] any() default {};

  /**
   * Alias for {@link #any()}.
   *
   * @see #any()
   */
  @AliasFor("any")
  String[] value() default {};

}
