package jp.fhub.fhub_feeling.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import jp.fhub.fhub_feeling.entity.Diary;
import jp.fhub.fhub_feeling.entity.User;

public interface DiaryRepository extends JpaRepository<Diary, UUID> {
    List<Diary> findTop3ByUserOrderByCreatedAtDesc(User user);
    List<Diary> findTop3ByUser_HospitalUsers_Hospital_IdOrderByCreatedAtDesc(UUID hospitalId);
    List<Diary> findTop3ByOrderByCreatedAtDesc();
    
    List<Diary> findByUserOrderByCreatedAtDesc(User user);
    List<Diary> findByUser_hospitalUsers_Hospital_IdOrderByCreatedAtDesc(UUID hospitalId);
    List<Diary> findByOrderByCreatedAtDesc();
    
    Optional<Diary> findDiaryById(UUID id);

    List<Diary> findTop3ByUserIdOrderByCreatedAtDesc(UUID id);
    int countByUserId(UUID id);
    List<LocalDate> findAllDatesByUserId(UUID userId);


}
