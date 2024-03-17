package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

import static org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest.toAnyEndpoint;
import static org.springframework.security.config.Customizer.withDefaults;

import ch.valtech.kubernetes.microservice.cluster.filestorage.util.SecurityUtils;
import ch.valtech.kubernetes.microservice.cluster.security.config.KeycloakRealmRoleConverter;
import io.grpc.CallCredentials;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
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
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

  private static final String ROLE_ACTUATOR = "actuator";
  private static final String ACTUATOR_REALM = "Actuator";
  private static final String ROLE_TOGGLZ = "togglz";
  private static final String TOGGLZ_REALM = "Togglz";

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

  @Bean
  @Order(0)
  public SecurityFilterChain filterChainApp(HttpSecurity http,
      @Value("${application.hostname}") String hostname) throws Exception {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());

    CookieCsrfTokenRepository csrfRepo = CookieCsrfTokenRepository.withHttpOnlyFalse();
    csrfRepo.setCookieCustomizer(cookie -> cookie.domain(URI.create(hostname).getHost()));
    csrfRepo.setCookiePath("/");

    http
        .securityMatcher("/api/**")
        .csrf(csrf -> csrf
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
        )
        .cors(withDefaults())
        .headers(headers -> headers
            .contentSecurityPolicy(policy -> policy
                .policyDirectives("default-src 'self'; "
                    + "connect-src 'self' https://monitoring.aks-demo.vtch.tech; "
                    + "frame-src 'self' data:; "
                    + "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; "
                    + "style-src 'self'; "
                    + "form-action 'self' data:; "
                    + "frame-ancestors 'self'; "
                    + "img-src 'self' data:; "
                    + "font-src 'self' data:"))
            .referrerPolicy(policy -> policy
                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            .frameOptions(FrameOptionsConfig::deny)
            .permissionsPolicy(policy -> policy
                .policy("geolocation=(none); "
                    + "midi=(none); "
                    + "sync-xhr=(none); "
                    + "microphone=(none); "
                    + "camera=(none); "
                    + "magnetometer=(none); "
                    + "gyroscope=(none); "
                    + "fullscreen=(self); "
                    + "payment=(none)")))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authz -> authz
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
    return http.build();
  }

  @Bean
  @Order(1)
  public SecurityFilterChain filterChainActuator(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/actuator/**")
        .authorizeHttpRequests(authz -> authz
            .requestMatchers(toAnyEndpoint().excluding(HealthEndpoint.class))
            .hasRole(ROLE_ACTUATOR)
            .requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
            .anyRequest().denyAll()
        )
        .httpBasic(basic -> basic.realmName(ACTUATOR_REALM));
    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain filterChainTogglz(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/togglz/**")
        .authorizeHttpRequests(authz -> authz
            .anyRequest().hasRole(ROLE_TOGGLZ)
        )
        .httpBasic(basic -> basic.realmName(TOGGLZ_REALM));
    return http.build();
  }

  @Bean
  public CallCredentials bearerAuthForwardingCredentials() {
    return CallCredentialsHelper.bearerAuth(() -> SecurityUtils.getJwt().orElse(UUID.randomUUID().toString()));
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
