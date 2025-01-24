package jp.fhub.fhub_feeling.dto.responsedto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopPageResponseDto {
    private String lastName;
    private List<DiaryResponseDto> diaries;
} 