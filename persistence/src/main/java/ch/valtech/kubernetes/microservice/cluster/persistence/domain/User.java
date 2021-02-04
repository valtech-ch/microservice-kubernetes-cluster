package ch.valtech.kubernetes.microservice.cluster.persistence.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Logged in
 */
@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
public class User {
  
  @Id
  @Column(name = "email")
  private String email ;//= "anonymous";
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
}
