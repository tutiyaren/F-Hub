package jp.fhub.fhub_feeling.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jp.fhub.fhub_feeling.service.UserOperationService;
import jp.fhub.fhub_feeling.dto.responsedto.UserResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.UserShowResponseDto;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserOperationService userOperationService;

    public UserController(UserOperationService userOperationService) {
        this.userOperationService = userOperationService;
    }

    @GetMapping("")
    public ResponseEntity<List<UserResponseDto>> list() {
        List<UserResponseDto> userResponseDto = userOperationService.getUserList();
        return ResponseEntity.ok().body(userResponseDto);
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserShowResponseDto> show(@PathVariable UUID userId) {
        UserShowResponseDto userShowResponseDto = userOperationService.showUser(userId);
        return ResponseEntity.ok().body(userShowResponseDto);
    }
    
}
