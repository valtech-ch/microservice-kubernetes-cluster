package ch.valtech.kubernetes.microservice.cluster.authentication.mock;

import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockOauth2ScopeSecurityContextFactory implements WithSecurityContextFactory<WithMockOauth2Scope> {

  @Override
  public SecurityContext createSecurityContext(WithMockOauth2Scope mockOauth2Scope) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    Set<String> scope = new HashSet<>();
    scope.add(mockOauth2Scope.scope());

    OAuth2Request request = new OAuth2Request(null, null, null, true, scope, null, null, null, null);

    Authentication auth = new OAuth2Authentication(request, null);

    context.setAuthentication(auth);

    return context;
  }

}