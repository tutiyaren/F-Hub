package jp.fhub.fhub_feeling.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import jp.fhub.fhub_feeling.entity.User;

public interface UserRepository extends JpaRepository<User, String>{
    Optional<User> findByEmail(String email);
}
