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
        .httpBasic().realmName("togglz")
        .and()
        .authorizeRequests()
        .anyRequest().hasRole("togglz");
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser(consoleUsername)
        .password(passwordEncoder().encode(consolePassword))
        .roles("togglz");
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
