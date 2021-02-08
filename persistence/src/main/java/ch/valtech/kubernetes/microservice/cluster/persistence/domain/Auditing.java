package ch.valtech.kubernetes.microservice.cluster.persistence.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Audits the attempt from a {@link User} to modify a {@link Message}
 */
@Entity
@Table(name = "auditing")
@NoArgsConstructor
@AllArgsConstructor
public class Auditing {
  
  @Id
  @GeneratedValue
  private Long id;
  
  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "email")
  private User user;
  
  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "keyId")
  private Message message;
  
  @Column(name = "modificatationDate")
  private LocalDate modificationDate;
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public Message getMessage() {
    return message;
  }
  
  public void setMessage(Message message) {
    this.message = message;
  }
  
  public LocalDate getModificationDate() {
    return modificationDate;
  }
  
  public void setModificationDate(LocalDate modificationDate) {
    this.modificationDate = modificationDate;
  }
}
