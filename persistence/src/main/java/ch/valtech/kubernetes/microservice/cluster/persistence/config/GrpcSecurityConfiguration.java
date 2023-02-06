package ch.valtech.kubernetes.microservice.cluster.persistence.config;

import ch.valtech.kubernetes.microservice.cluster.security.config.IstioJwtDecoder;
import ch.valtech.kubernetes.microservice.cluster.security.config.KeycloakRealmRoleConverter;
import org.lognet.springboot.grpc.security.GrpcSecurity;
import org.lognet.springboot.grpc.security.GrpcSecurityConfigurerAdapter;
import org.lognet.springboot.grpc.security.jwt.JwtAuthProviderFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

@Configuration
public class GrpcSecurityConfiguration extends GrpcSecurityConfigurerAdapter {

  @Override
  public void configure(GrpcSecurity builder) throws Exception {
    JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
    authenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
    JwtAuthenticationProvider authenticationProvider = JwtAuthProviderFactory.forAuthorities(
        getContext().getBean(IstioJwtDecoder.class));
    authenticationProvider.setJwtAuthenticationConverter(authenticationConverter);
    builder.authorizeRequests()
        .anyMethod().authenticated()
        .and()
        .authenticationProvider(authenticationProvider);
  }

}
