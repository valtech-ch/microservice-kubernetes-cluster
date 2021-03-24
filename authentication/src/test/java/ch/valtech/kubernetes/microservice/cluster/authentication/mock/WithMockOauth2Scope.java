package ch.valtech.kubernetes.microservice.cluster.authentication.mock;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockOauth2ScopeSecurityContextFactory.class)
public @interface WithMockOauth2Scope {

  String scope() default "";

}