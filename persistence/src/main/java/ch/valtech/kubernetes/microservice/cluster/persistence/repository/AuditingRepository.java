package ch.valtech.kubernetes.microservice.cluster.persistence.repository;

import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuditingRepository extends JpaRepository<Auditing, Long> {

}
