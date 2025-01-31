package jp.fhub.fhub_feeling.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import jp.fhub.fhub_feeling.entity.User;

public interface UserRepository extends JpaRepository<User, UUID>{
    Optional<User> findByEmail(String email);

    List<User> findByHospitalUsers_Hospital_IdOrderByCreatedAtDesc(UUID hospitalId);
    List<User> findAllByOrderByCreatedAtDesc();
    
    Optional<User> findUserById(UUID id);
}
