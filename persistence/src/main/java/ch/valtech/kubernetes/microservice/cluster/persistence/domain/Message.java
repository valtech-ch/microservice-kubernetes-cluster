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
 * Represents a message
 */
@Data
@ToString
@Entity
@Table(name = "message")
@NoArgsConstructor
@AllArgsConstructor
public class Message {

  @Id
  @Column(name = "key_id")
  private String keyId;

  @Column(name = "value")
  private String value;

}
