package ch.valtech.kubernetes.microservice.cluster.persistence.repository;

import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditingRepository extends JpaRepository<Auditing, Long> {

}
