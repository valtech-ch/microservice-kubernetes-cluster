package ch.valtech.kubernetes.microservice.cluster.persistence.domain;

import lombok.*;

import javax.persistence.*;

/**
 * Represents a message
 */


@Entity
@Table(name = "message")
@NoArgsConstructor
@AllArgsConstructor
public class Message {
  
  @Id
  @Column(name = "keyId")
  private String keyId;
  @Column(name = "value")
  private String value;
  
  
  
  public String getKeyId() {
    return keyId;
  }
  
  public void setKeyId(String key) {
    this.keyId = key;
  }
  
  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    this.value = value;
    
  }
  
  @Override
  public String toString() {
    return "Message{" +
        ", key='" + keyId + '\'' +
        ", value='" + value + '\'' +
        '}';
  }
}
