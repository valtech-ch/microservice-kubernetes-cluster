package ch.valtech.kubernetes.microservice.cluster.persistence.repository;

import ch.valtech.kubernetes.microservice.cluster.persistence.domain.Message;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends CrudRepository<Message, String> {

  Optional<Message> findByKeyId(@Param("key") String key);

}
