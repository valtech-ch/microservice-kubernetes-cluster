package ch.valtech.kubernetes.microservice.cluster.authentication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  public static final String ROLE_ADMIN = "ADMIN";
  public static final String ROLE_USER = "USER";
  private String user;
  private String userPassword;
  private String admin;
  private String adminPassword;

  public SecurityConfiguration(
      @Value("${application.user.username}") String user,
      @Value("${application.user.password}") String userPassword,
      @Value("${application.admin.username}") String admin,
      @Value("${application.admin.password}") String adminPassword) {
    this.user = user;
    this.userPassword = userPassword;
    this.admin = admin;
    this.adminPassword = adminPassword;
  }

  @Bean
  @Override
  protected UserDetailsService userDetailsService() {
    // User Role
    UserDetails appUser = User.withUsername(user)
        .passwordEncoder(passwordEncoder()::encode)
        .password(userPassword)
        .roles(ROLE_USER)
        .build();
    // Manager Role
    UserDetails adminUser = User.withUsername(admin)
        .passwordEncoder(passwordEncoder()::encode)
        .password(adminPassword)
        .roles(ROLE_ADMIN)
        .build();
    return new InMemoryUserDetailsManager(adminUser, appUser);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

}