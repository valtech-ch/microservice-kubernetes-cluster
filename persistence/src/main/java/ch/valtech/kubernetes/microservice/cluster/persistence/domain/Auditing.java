package ch.valtech.kubernetes.microservice.cluster.persistence.domain;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Audits the changes made to the filesystem.
 */
@Data
@Builder
@ToString
@Table("auditing")
@NoArgsConstructor
@AllArgsConstructor
public class Auditing {

  @Id
  private Long id;

  @Column("modification_date")
  private LocalDate modificationDate;

  @NonNull
  @Column("username")
  private String username;

  @NonNull
  @Column("filename")
  private String filename;

  @NonNull
  @Column("action")
  private Action action;

}
