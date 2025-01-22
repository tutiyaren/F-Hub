package jp.fhub.fhub_feeling.controller;

import jp.fhub.fhub_feeling.dto.requestdto.LoginRequestDto;
import jp.fhub.fhub_feeling.dto.requestdto.RegisterRequestDto;
import jp.fhub.fhub_feeling.dto.responsedto.LoginResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.LogoutResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.MeResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.RefreshResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.RegisterResponseDto;
import jp.fhub.fhub_feeling.service.UserService;
import jp.fhub.fhub_feeling.service.ValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final ValidationService validationService;

    public AuthController(
            UserService userService,
            ValidationService validationService
    ) {
        this.userService = userService;
        this.validationService = validationService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@Valid @RequestBody RegisterRequestDto request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = validationService.extractFirstErrorMessage(bindingResult);
            return ResponseEntity.unprocessableEntity().body(new RegisterResponseDto(errorMessage));
        }
        userService.registerUser(request);
        RegisterResponseDto response = new RegisterResponseDto("会員登録が完了しました。");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = validationService.extractFirstErrorMessage(bindingResult);
            return ResponseEntity.unprocessableEntity().body(new LoginResponseDto(errorMessage));
        }
        LoginResponseDto loginResponse = userService.loginUser(loginRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponseDto> me(HttpServletRequest request) {   
        MeResponseDto meResponse = userService.meUser(request);
        return ResponseEntity.ok(meResponse);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponseDto> refresh(HttpServletRequest request) {
        RefreshResponseDto refreshResponse = userService.refreshUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(refreshResponse);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDto> logout(HttpServletRequest request) {
        LogoutResponseDto logoutResponse = userService.logoutUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(logoutResponse);
    }
}
