package jp.fhub.fhub_feeling.dto.responsedto;

import java.time.LocalDateTime;
import java.util.UUID;

import jp.fhub.fhub_feeling.entity.Diary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiaryResponseDto {
    private UUID id;
    private int moodScore;
    private String goodContents;
    private String contents;
    private LocalDateTime createdAt;
    private String firstName;
    private String lastName;
    
    public static DiaryResponseDto fromEntity(Diary diary) {
        return new DiaryResponseDto(
            diary.getId(),
            diary.getMoodScore(),
            diary.getGoodContents(),
            diary.getContents(),
            diary.getCreatedAt(),
            diary.getUser().getFirstName(),
            diary.getUser().getLastName());
    }
}
