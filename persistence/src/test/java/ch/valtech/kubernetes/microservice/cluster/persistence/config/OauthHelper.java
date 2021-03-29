package ch.valtech.kubernetes.microservice.cluster.persistence.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@Component
@AllArgsConstructor
public class OauthHelper {

  ClientDetailsService clientDetailsService;
  UserDetailsService userDetailsService;
  AuthorizationServerTokenServices tokenService;

  public RequestPostProcessor bearerToken(final String clientId, final String username) {
    return mockRequest -> {
      OAuth2Authentication auth = oauth2Authentication(clientId, username);
      OAuth2AccessToken token = tokenService.createAccessToken(auth);
      mockRequest.addHeader("Authorization", "Bearer " + token.getValue());
      return mockRequest;
    };
  }

  public OAuth2Authentication oauth2Authentication(final String clientId, final String username) {
    ClientDetails client = clientDetailsService.loadClientByClientId(clientId);
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + username.toUpperCase());
    List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
    authorities.add(authority);
    Set<String> resourceIds = client.getResourceIds();
    Set<String> scopes = client.getScope();

    // Default values for other parameters
    Map<String, String> requestParameters = Collections.emptyMap();
    boolean approved = true;
    String redirectUrl = null;
    Set<String> responseTypes = Collections.emptySet();
    Map<String, Serializable> extensionProperties = Collections.emptyMap();

    // Create request
    OAuth2Request oauth2Request = new OAuth2Request(requestParameters, clientId, authorities, approved, scopes,
        resourceIds, redirectUrl, responseTypes, extensionProperties);

    // Create OAuth2AccessToken
    UserDetails user = userDetailsService.loadUserByUsername(username);
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null,
        authorities);
    return new OAuth2Authentication(oauth2Request, authenticationToken);
  }
}
