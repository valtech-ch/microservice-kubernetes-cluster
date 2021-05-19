package ch.valtech.kubernetes.microservice.cluster.filestorage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Order(2)
@Configuration
@EnableWebSecurity
public class TogglzSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private static final String ROLE_TOGGLZ = "togglz";
  private static final String TOGGLZ_REALM = "Togglz";

  private final String consoleUsername;
  private final String consolePassword;

  public TogglzSecurityConfiguration(
      @Value("${togglz.console.username}") String consoleUsername,
      @Value("${togglz.console.password}") String consolePassword) {
    this.consoleUsername = consoleUsername;
    this.consolePassword = consolePassword;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .antMatcher("/togglz/**")
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .httpBasic().realmName(TOGGLZ_REALM)
        .and()
        .authorizeRequests()
        .anyRequest().hasRole(ROLE_TOGGLZ);
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser(consoleUsername)
        .password(passwordEncoder().encode(consolePassword))
        .roles(ROLE_TOGGLZ);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
