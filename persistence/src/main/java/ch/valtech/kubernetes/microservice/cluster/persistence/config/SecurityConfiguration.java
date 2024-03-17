package ch.valtech.kubernetes.microservice.cluster.persistence.config;

import static org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest.toAnyEndpoint;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.PersistenceServiceGrpc;
import ch.valtech.kubernetes.microservice.cluster.persistence.api.grpc.ReactorPersistenceServiceGrpc;
import ch.valtech.kubernetes.microservice.cluster.security.config.IstioJwtDecoder;
import ch.valtech.kubernetes.microservice.cluster.security.config.KeycloakRealmRoleConverter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import net.devh.boot.grpc.server.security.authentication.BearerAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.check.AccessPredicate;
import net.devh.boot.grpc.server.security.check.AccessPredicateVoter;
import net.devh.boot.grpc.server.security.check.GrpcSecurityMetadataSource;
import net.devh.boot.grpc.server.security.check.ManualGrpcSecurityMetadataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.HeaderBearerTokenResolver;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
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

  @Bean
  public InMemoryUserDetailsManager userDetailsService(
      @Value("${management.security.username}") String actuatorUsername,
      @Value("${management.security.password}") String actuatorPassword) {
    UserDetails actuatorUser = User
        .withUsername(actuatorUsername)
        .password(passwordEncoder().encode(actuatorPassword))
        .roles(ROLE_ACTUATOR)
        .build();
    return new InMemoryUserDetailsManager(actuatorUser);
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
  public GrpcAuthenticationReader grpcAuthenticationReader() {
    return new BearerAuthenticationReader(token -> new BearerTokenAuthenticationToken(token));
  }

  @Bean
  public AuthenticationManager authenticationManager(IstioJwtDecoder jwtDecoder) {
    JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
    authenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
    final List<AuthenticationProvider> providers = new ArrayList<>();
    JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
    authenticationProvider.setJwtAuthenticationConverter(authenticationConverter);
    providers.add(authenticationProvider);
    return new ProviderManager(providers);
  }

  @Bean
  public GrpcSecurityMetadataSource grpcSecurityMetadataSource() {
    final ManualGrpcSecurityMetadataSource source = new ManualGrpcSecurityMetadataSource();
    source.set(PersistenceServiceGrpc.getAuditMethod(), AccessPredicate.hasAnyRole("ROLE_admin", "ROLE_user"));
    source.set(PersistenceServiceGrpc.getSearchMethod(), AccessPredicate.hasRole("ROLE_admin"));
    source.setDefault(AccessPredicate.authenticated());
    return source;
  }

  @Bean
  public AccessDecisionManager accessDecisionManager() {
    final List<AccessDecisionVoter<?>> voters = new ArrayList<>();
    voters.add(new AccessPredicateVoter());
    return new UnanimousBased(voters);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource(
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
