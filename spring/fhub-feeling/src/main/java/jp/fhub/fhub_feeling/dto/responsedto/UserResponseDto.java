package jp.fhub.fhub_feeling.dto.responsedto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import jp.fhub.fhub_feeling.entity.Diary;
import jp.fhub.fhub_feeling.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private String hospitalName;
    private int streakCount;

    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getCreatedAt(),
                user.getHospitalUsers().isEmpty() ? null :user.getHospitalUsers().get(0).getHospital().getName(),
                calculateStreak(user));
    }
    
    private static int calculateStreak(User user) {
        List<Diary> diaries = user.getDiaries();
        if (diaries.isEmpty()) return 0;
        
        diaries.sort(Comparator.comparing(Diary::getCreatedAt).reversed());
        LocalDate today = LocalDate.now();
        int streak = 0;

        for (Diary diary : diaries) {
            LocalDate diaryDate = diary.getCreatedAt().toLocalDate();

            if (streak == 0) {
                if (!diaryDate.isEqual(today.minusDays(1))) {
                    return streak; 
                }
                if (!diaryDate.isEqual(today)) {
                    streak = 1; 
                }
            } else { 
                if (!diaryDate.isEqual(today.minusDays((long) streak + 1))) {
                    return streak;
                }
                streak++;
            }
        }
        
        return streak;
    }
}
