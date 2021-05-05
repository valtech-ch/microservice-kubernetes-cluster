package ch.valtech.kubernetes.microservice.cluster.persistence.domain;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

/**
 * Audits the changes made to the filesystem.
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

  @Column(name = "modification_date")
  private LocalDate modificationDate;

  @NonNull
  @Column(name = "username")
  private String username;

  @NonNull
  @Column(name = "filename")
  private String filename;

  @NonNull
  @Enumerated
  @Column(name = "action")
  private Action action;

}
