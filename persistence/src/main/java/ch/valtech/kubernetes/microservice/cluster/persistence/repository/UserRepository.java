package ch.valtech.kubernetes.microservice.cluster.persistence.repository;

import ch.valtech.kubernetes.microservice.cluster.persistence.domain.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

  Optional<User> findByEmail(@Param("email") String email);

}
