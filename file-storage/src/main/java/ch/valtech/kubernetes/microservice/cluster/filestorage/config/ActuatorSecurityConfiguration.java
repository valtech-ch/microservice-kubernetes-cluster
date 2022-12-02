package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ActuatorSecurityConfiguration {

  private static final String ROLE_ACTUATOR = "actuator";
  private static final String ACTUATOR_REALM = "Actuator";

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authz -> authz
            .requestMatchers(EndpointRequest.toAnyEndpoint()
                .excluding(HealthEndpoint.class))
            .hasRole(ROLE_ACTUATOR)
            .anyRequest().permitAll()
        )
        .httpBasic(basic -> basic.realmName(ACTUATOR_REALM));
    return http.build();
  }

  @Bean
  public InMemoryUserDetailsManager userDetailsService(
      @Value("${management.security.username}") String actuatorUsername,
      @Value("${management.security.password}") String actuatorPassword) {
    UserDetails user = User
        .withUsername(actuatorUsername)
        .password(passwordEncoder().encode(actuatorPassword))
        .roles(ROLE_ACTUATOR)
        .build();
    return new InMemoryUserDetailsManager(user);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
