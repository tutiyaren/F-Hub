package jp.fhub.fhub_feeling.dto.responsedto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserShowDiaryResponseDto {
    private UUID diaryId;
    private int moodScore;
    private String goodContents;
    private String contents;
    private LocalDateTime createdAt;
}
