package jp.fhub.fhub_feeling.dto.responsedto;

import java.time.LocalDateTime;
import java.util.UUID;

import jp.fhub.fhub_feeling.entity.Diary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopPageResponseDto {
    private String lastName;
    private UUID id;
    private int moodScore;
    private String goodContents;
    private String contents;
    private LocalDateTime createdAt;

    public static TopPageResponseDto fromEntity(Diary diary) {
        return new TopPageResponseDto(
                diary.getUser().getLastName(),
                diary.getId(),
                diary.getMoodScore(),
                diary.getGoodContents(),
                diary.getContents(),
                diary.getCreatedAt());
    }
} 