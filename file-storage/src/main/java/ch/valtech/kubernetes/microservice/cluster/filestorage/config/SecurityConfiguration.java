package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

import ch.valtech.kubernetes.microservice.cluster.security.config.KeycloakRealmRoleConverter;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Order(0)
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final List<String> allowedOrigins;
  private final List<String> allowedMethods;

  public SecurityConfiguration(@Value("${application.cors.allowed.origins}") List<String> allowedOrigins,
      @Value("${application.cors.allowed.methods}") List<String> allowedMethods) {
    this.allowedOrigins = allowedOrigins;
    this.allowedMethods = allowedMethods;
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());

    http.csrf().disable()
        .cors(Customizer.withDefaults()) // by default uses a Bean by the name of corsConfigurationSource
        .headers()
        .contentSecurityPolicy("default-src 'self'; "
            + "connect-src 'self' https://vtch-aks-demo-monitoring.duckdns.org; "
            + "frame-src 'self' data:; "
            + "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; "
            + "style-src 'self' 'unsafe-inline'; "
            + "img-src 'self' data:; "
            + "font-src 'self' data:")
        .and()
        .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
        .and()
        .permissionsPolicy(permissions -> permissions.policy("geolocation=(none); "
            + "midi=(none); "
            + "sync-xhr=(none); "
            + "microphone=(none); "
            + "camera=(none); "
            + "magnetometer=(none); "
            + "gyroscope=(none); "
            + "fullscreen=(self); "
            + "payment=(none)"))
        .and()
        .frameOptions().deny()
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .antMatcher("/api/**")
        .authorizeRequests()
        .anyRequest().authenticated()
        .and()
        .oauth2ResourceServer().jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter));
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(allowedOrigins);
    configuration.setAllowedMethods(allowedMethods);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);
    source.registerCorsConfiguration("/v2/api-docs", configuration);
    return source;
  }
}
