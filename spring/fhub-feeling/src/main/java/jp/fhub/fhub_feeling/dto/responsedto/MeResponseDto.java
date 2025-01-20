package jp.fhub.fhub_feeling.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MeResponseDto {
    private String message;
    private String firstName;
    private String lastName;
    private String email;
    private String roleName;

    public MeResponseDto(String firstName, String lastName, String email, String roleName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roleName = roleName;
    }

    public MeResponseDto(String message) {
        this.message = message;
    }
}
