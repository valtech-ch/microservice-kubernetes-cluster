package ch.valtech.kubernetes.microservice.cluster.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppUser {

  private String username;

  private String password;

  private String role;

}
