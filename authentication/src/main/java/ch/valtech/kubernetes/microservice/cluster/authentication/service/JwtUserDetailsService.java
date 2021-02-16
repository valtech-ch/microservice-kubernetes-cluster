package ch.valtech.kubernetes.microservice.cluster.authentication.service;

import ch.valtech.kubernetes.microservice.cluster.authentication.model.AppUser;
import ch.valtech.kubernetes.microservice.cluster.authentication.model.UserRole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

  public static final String USER = "USER";
  public static final String ADMIN = "ADMIN";
  @Autowired
  private PasswordEncoder encoder;


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AppUser user = getUserByUsername(username);
      if (user == null) {
      throw new UsernameNotFoundException("User not found with username: " + username);
    }

    return getUserDetails(user);
  }

  private User getUserDetails(AppUser user) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    if (user.getRole().contains(USER)){
      authorities.add(new SimpleGrantedAuthority(UserRole.ROLE_USER.name()));
    }
    if (user.getRole().contains(ADMIN)){
      authorities.add(new SimpleGrantedAuthority(UserRole.ROLE_ADMIN.name()));
    }
    return new User(user.getUsername(), user.getPassword(), authorities);
  }

  private AppUser getUserByUsername(String username) {
    return getUsers().stream().filter(appUser -> username.equals(appUser.getUsername())).findFirst().orElse(null);
  }

  private List<AppUser> getUsers() {
    return Arrays.asList(
        new AppUser("user", encoder.encode("12345"), USER),
        new AppUser("admin", encoder.encode("12345"), ADMIN)
    );
  }


}
