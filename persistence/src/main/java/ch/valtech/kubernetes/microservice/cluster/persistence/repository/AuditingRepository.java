package ch.valtech.kubernetes.microservice.cluster.persistence.repository;

import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Auditing;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AuditingRepository extends ReactiveCrudRepository<Auditing, Long> {

  Flux<Auditing> findByFilename(String filename, Pageable pageable);

  Flux<Auditing> findBy(Pageable pageable);

}
