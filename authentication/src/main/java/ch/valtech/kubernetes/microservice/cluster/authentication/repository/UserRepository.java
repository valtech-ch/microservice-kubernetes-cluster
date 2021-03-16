package ch.valtech.kubernetes.microservice.cluster.authentication.repository;

import ch.valtech.kubernetes.microservice.cluster.authentication.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
