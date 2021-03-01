package ch.valtech.kubernetes.microservice.cluster.authentication.web.rest;

import ch.valtech.kubernetes.microservice.cluster.authentication.service.jwt.JwtTokenUtil;
import ch.valtech.kubernetes.microservice.cluster.authentication.model.JwtResponse;
import ch.valtech.kubernetes.microservice.cluster.authentication.model.UserCredentials;
import ch.valtech.kubernetes.microservice.cluster.authentication.service.JwtUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Files.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;

  private final JwtTokenUtil jwtTokenUtil;

  private final JwtUserDetailsService userDetailsService;

  public AuthenticationController(AuthenticationManager authenticationManager,
      JwtTokenUtil jwtTokenUtil,
      JwtUserDetailsService userDetailsService) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenUtil = jwtTokenUtil;
    this.userDetailsService = userDetailsService;
  }

  @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
  public ResponseEntity<?> createAuthenticationToken(@RequestBody UserCredentials authenticationRequest)
      throws Exception {

    Authentication authentication = authenticate(authenticationRequest.getUsername(),
        authenticationRequest.getPassword());
    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

    String token = jwtTokenUtil.generateToken(userDetails, authentication);

    return ResponseEntity.ok(new JwtResponse(token));
  }

  private Authentication authenticate(String username, String password) throws Exception {
    try {
      return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    } catch (DisabledException e) {
      throw new Exception("USER_DISABLED", e);
    } catch (BadCredentialsException e) {
      throw new Exception("INVALID_CREDENTIALS", e);
    }
  }

}