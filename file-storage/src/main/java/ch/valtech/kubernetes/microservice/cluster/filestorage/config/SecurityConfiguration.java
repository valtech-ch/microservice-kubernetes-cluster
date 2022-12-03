package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

import static org.springframework.security.config.Customizer.withDefaults;

import ch.valtech.kubernetes.microservice.cluster.security.config.KeycloakRealmRoleConverter;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

  private static final String ROLE_ACTUATOR = "actuator";
  private static final String ROLE_TOGGLZ = "togglz";

  @Bean
  public InMemoryUserDetailsManager userDetailsService(
      @Value("${management.security.username}") String actuatorUsername,
      @Value("${management.security.password}") String actuatorPassword,
      @Value("${togglz.console.username}") String consoleUsername,
      @Value("${togglz.console.password}") String consolePassword) {
    UserDetails actuatorUser = User
        .withUsername(actuatorUsername)
        .password(passwordEncoder().encode(actuatorPassword))
        .roles(ROLE_ACTUATOR)
        .build();
    UserDetails togglzUser = User
        .withUsername(consoleUsername)
        .password(passwordEncoder().encode(consolePassword))
        .roles(ROLE_TOGGLZ)
        .build();
    return new InMemoryUserDetailsManager(actuatorUser, togglzUser);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Order(0)
  @Configuration
  public static class AppSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChainApp(HttpSecurity http,
        @Value("${application.hostname}") String hostname) throws Exception {
      JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
      jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());

      CookieCsrfTokenRepository csrfRepo = CookieCsrfTokenRepository.withHttpOnlyFalse();
      csrfRepo.setCookieDomain(URI.create(hostname).getHost());
      csrfRepo.setCookiePath("/");

      http.csrf()
          .csrfTokenRepository(csrfRepo)
          .withObjectPostProcessor(new ObjectPostProcessor<CsrfFilter>() {
            /**
             * Overrides required csrf protection matcher because of OAuth2 registering a wrong ignore matcher.
             * https://github.com/spring-projects/spring-security/issues/8668
             */
            @Override
            public <O extends CsrfFilter> O postProcess(O object) {
              object.setRequireCsrfProtectionMatcher(CsrfFilter.DEFAULT_CSRF_MATCHER);
              return object;
            }
          })
          .and()
          .cors(withDefaults()) // by default uses a Bean by the name of corsConfigurationSource
          .headers()
          .contentSecurityPolicy("default-src 'self'; "
              + "connect-src 'self' https://vtch-aks-demo-monitoring.duckdns.org; "
              + "frame-src 'self' data:; "
              + "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; "
              + "style-src 'self'; "
              + "form-action 'self' data:; "
              + "frame-ancestors 'self'; "
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
          .authorizeHttpRequests(authz -> authz
              .requestMatchers("/api/**").authenticated()
              .anyRequest().authenticated()
          )
          .oauth2ResourceServer().jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter));
      return http.build();
    }

  }

  @Order(1)
  @Configuration
  public static class ActuatorSecurityConfiguration {

    private static final String ACTUATOR_REALM = "Actuator";

    @Bean
    public SecurityFilterChain filterChainActuator(HttpSecurity http) throws Exception {
      http
          .authorizeHttpRequests(authz -> authz
              .requestMatchers(EndpointRequest.toAnyEndpoint()
                  .excluding(HealthEndpoint.class))
              .hasRole(ROLE_ACTUATOR)
          )
          .httpBasic(basic -> basic.realmName(ACTUATOR_REALM));
      return http.build();
    }

  }

  @Order(2)
  @Configuration
  public static class TogglzSecurityConfiguration {

    private static final String TOGGLZ_REALM = "Togglz";

    @Bean
    public SecurityFilterChain filterChainTogglz(HttpSecurity http) throws Exception {
      http
          .authorizeHttpRequests(authz -> authz
              .requestMatchers("/togglz/**")
              .hasRole(ROLE_TOGGLZ)
          )
          .httpBasic(basic -> basic.realmName(TOGGLZ_REALM));
      return http.build();
    }

  }

  @Bean
  CorsConfigurationSource corsConfigurationSource(
      @Value("${application.cors.allowed.origins}") List<String> allowedOrigins,
      @Value("${application.cors.allowed.methods}") List<String> allowedMethods) {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(allowedOrigins);
    configuration.setAllowedMethods(allowedMethods);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);
    source.registerCorsConfiguration("/v2/api-docs", configuration);
    return source;
  }

}
