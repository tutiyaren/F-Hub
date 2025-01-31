package jp.fhub.fhub_feeling.dto.responsedto;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserShowResponseDto {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private List<UserShowDiaryResponseDto> diaries;
    private String roleName;
    private UUID hospitalId;
    private String hospitalName;
    private int streakCount;
    private int totalCount;
}
