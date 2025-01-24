package jp.fhub.fhub_feeling.dto.responsedto;

import java.time.LocalDateTime;

import jp.fhub.fhub_feeling.entity.Diary;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DiaryResponseDto {
    private Integer moodScore;
    private String goodContents;
    private String contents;
    private LocalDateTime createdAt;

    public static DiaryResponseDto fromEntity(Diary diary) {
        return new DiaryResponseDto(
            diary.getMoodScore(),
            diary.getGoodContents(),
            diary.getContents(),
            diary.getCreatedAt()
        );
    }
}
