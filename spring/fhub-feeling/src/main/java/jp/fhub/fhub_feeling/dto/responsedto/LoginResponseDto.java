package jp.fhub.fhub_feeling.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class LoginResponseDto {
    private String token;
    private String roleName;
}
