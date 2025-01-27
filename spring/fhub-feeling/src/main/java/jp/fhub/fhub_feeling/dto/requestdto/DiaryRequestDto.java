package jp.fhub.fhub_feeling.dto.requestdto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class DiaryRequestDto {

    @NotNull(message = "10段階評価を選択してください")
    @Min(value = 1, message = "1 ~ 10の範囲で入力してください")
    @Max(value = 10, message = "1 ~ 10の範囲で入力してください")
    private Integer moodScore;

    @NotBlank(message = "良かったことを入力してください")
    @Size(max = 255, message = "良かったことは255文字以内で入力してください")
    private String goodContents;

    @NotBlank(message = "日記の内容を入力してください")
    @Size(max = 255, message = "日記の内容は255文字以内で入力してください")
    private String contents;
}
