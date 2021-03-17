package ch.valtech.kubernetes.microservice.cluster.authentication.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableAuthorizationServer
public class AuthenticationServerConfiguration extends AuthorizationServerConfigurerAdapter {

  private final AuthenticationManager authenticationManager;

  private final PasswordEncoder passwordEncoder;

  private String clientId;

  private String clientSecret;

  private int accessTokenValiditySeconds;

  private String[] authorizedGrantTypes;

  private int refreshTokenValiditySeconds;

  private String hostname;

  private String signingKey;

  public AuthenticationServerConfiguration(AuthenticationManager authenticationManager,
      PasswordEncoder passwordEncoder,
      @Value("${jwt.client.id:kubernetes-cluster}") String clientId,
      @Value("${jwt.client.secret:secret}") String clientSecret,
      @Value("${jwt.authorizedGrantTypes:password,authorization_code,refresh_token}") String[] authorizedGrantTypes,
      @Value("${jwt.access.token.validity:43200}") int accessTokenValiditySeconds,
      @Value("${jwt.refresh.token.validity:2592000}") int refreshTokenValiditySeconds,
      @Value("${application.hostname}") String hostname,
      @Value("${application.signing.key}") String signingKey) {
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.authorizedGrantTypes = authorizedGrantTypes;
    this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    this.hostname = hostname;
    this.signingKey = signingKey;
  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.inMemory()
        .withClient(clientId)
        .secret(passwordEncoder.encode(clientSecret))
        .accessTokenValiditySeconds(accessTokenValiditySeconds)
        .refreshTokenValiditySeconds(refreshTokenValiditySeconds)
        .authorizedGrantTypes(authorizedGrantTypes)
        .scopes("read", "write");
  }

  @Override
  public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
    TokenEnhancerChain chain = new TokenEnhancerChain();
    chain.setTokenEnhancers(List.of(tokenEnhancer(), accessTokenConverter()));
    endpoints
        .accessTokenConverter(accessTokenConverter())
        .authenticationManager(authenticationManager)
        .tokenEnhancer(chain);
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
    oauthServer.checkTokenAccess("isAuthenticated()");
  }

  @Bean
  JwtAccessTokenConverter accessTokenConverter() {
    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    converter.setSigningKey(signingKey);
    return converter;
  }

  private TokenEnhancer tokenEnhancer() {
    return (accessToken, authentication) -> {
      Map<String, Object> additionalInfo = new HashMap<>();
      additionalInfo.put("iss", hostname);
      ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
      return accessToken;
    };
  }
}