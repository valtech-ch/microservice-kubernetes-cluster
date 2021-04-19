package ch.valtech.kubernetes.microservice.cluster.persistence.config;

import static org.springframework.security.oauth2.jwt.JwtDecoders.fromIssuerLocation;
import static org.springframework.security.oauth2.jwt.JwtValidators.createDefaultWithIssuer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> oauthConfig;

  public SecurityConfiguration(
      @Value("${spring.security.oauth2.enabled:true}") boolean enabledOauth2,
      @Value("${application.token.issuer}") String tokenIssuer) {
    if (enabledOauth2) {
      this.oauthConfig = oauthConfigEnabled(tokenIssuer);
    } else {
      this.oauthConfig = AbstractHttpConfigurer::disable;
    }
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .antMatcher("/api/**")
        .authorizeRequests()
        .anyRequest().authenticated()
        .and()
        .oauth2ResourceServer(oauthConfig);
  }

  private Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> oauthConfigEnabled(String tokenIssuer) {
    OAuth2TokenValidator<Jwt> jwtValidator = createDefaultWithIssuer(tokenIssuer);
    NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) fromIssuerLocation(tokenIssuer);
    jwtDecoder.setJwtValidator(jwtValidator);
    jwtDecoder.setClaimSetConverter(new UsernameSubClaimAdapter());

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
    return oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter).decoder(jwtDecoder));
  }

}
