package ch.valtech.kubernetes.microservice.cluster.persistence.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Order(1)
@Configuration
@EnableWebSecurity
public class ActuatorSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final String ROLE_ACTUATOR = "actuator";
  private final String ACTUATOR_REALM = "Actuator";

  private final String actuatorUsername;
  private final String actuatorPassword;

  public ActuatorSecurityConfiguration(
      @Value("${management.security.username}") String actuatorUsername,
      @Value("${management.security.password}") String actuatorPassword) {
    this.actuatorUsername = actuatorUsername;
    this.actuatorPassword = actuatorPassword;
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.requestMatcher(EndpointRequest.toAnyEndpoint()
        .excluding(InfoEndpoint.class, HealthEndpoint.class))
        .authorizeRequests().anyRequest().hasRole(ROLE_ACTUATOR)
        .and()
        .httpBasic().realmName(ACTUATOR_REALM);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser(actuatorUsername)
        .password(passwordEncoder().encode(actuatorPassword))
        .roles(ROLE_ACTUATOR);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
