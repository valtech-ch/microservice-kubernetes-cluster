package ch.valtech.kubernetes.microservice.cluster.authentication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableAuthorizationServer
public class AuthenticationServerConfiguration extends AuthorizationServerConfigurerAdapter {

  private final AuthenticationManager authenticationManager;

  private final PasswordEncoder passwordEncoder;

  private final UserDetailsService userService;

  private String clientId;

  private String clientSecret;

  private int accessTokenValiditySeconds;

  private String[] authorizedGrantTypes;

  private int refreshTokenValiditySeconds;

  public AuthenticationServerConfiguration(AuthenticationManager authenticationManager,
      PasswordEncoder passwordEncoder,
      UserDetailsService userService,
      @Value("${jwt.clientId:kubernetes-cluster}") String clientId,
      @Value("${jwt.client-secret:secret}") String clientSecret,
      @Value("${jwt.authorizedGrantTypes:password,authorization_code,refresh_token}") String[] authorizedGrantTypes,
      @Value("${jwt.accessTokenValiditySeconds:43200}") int accessTokenValiditySeconds,
      @Value("${jwt.refreshTokenValiditySeconds:2592000}") int refreshTokenValiditySeconds) {
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
    this.userService = userService;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.authorizedGrantTypes = authorizedGrantTypes;
    this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.inMemory()
        .withClient(clientId)
        .secret(passwordEncoder.encode(clientSecret))
        .accessTokenValiditySeconds(accessTokenValiditySeconds)
        .refreshTokenValiditySeconds(refreshTokenValiditySeconds)
        .authorizedGrantTypes(authorizedGrantTypes)
        .scopes("read", "write")
        .resourceIds("api");
  }

  @Override
  public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
    endpoints
        .accessTokenConverter(accessTokenConverter())
        .userDetailsService(userService)
        .authenticationManager(authenticationManager);
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
    oauthServer.checkTokenAccess("isAuthenticated()");
  }

  @Bean
  JwtAccessTokenConverter accessTokenConverter() {
    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    converter.setSigningKey("test");
    return converter;
  }

}