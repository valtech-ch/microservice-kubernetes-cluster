package ch.valtech.kubernetes.microservice.cluster.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Logged in.
 */
@Data
@ToString
@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @Id
  @Column(name = "email")
  private String email;

}
