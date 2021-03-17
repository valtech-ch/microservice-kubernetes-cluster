package ch.valtech.kubernetes.microservice.cluster.persistence.domain;


import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Audits the attempt from a {@link User} to modify a {@link Message}
 */
@Data
@ToString
@Entity
@Table(name = "auditing")
@NoArgsConstructor
@AllArgsConstructor
public class Auditing {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(cascade = CascadeType.PERSIST)
  private User user;

  @ManyToOne(cascade = CascadeType.PERSIST)
  private Message message;

  @Column(name = "modification_date")
  private LocalDate modificationDate;

}
