package jp.fhub.fhub_feeling.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import jp.fhub.fhub_feeling.entity.HospitalUser;

public interface HospitalUserRepository extends JpaRepository<HospitalUser, UUID> {
    Optional<HospitalUser> findByUserId(UUID id);
}