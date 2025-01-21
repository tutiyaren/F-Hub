package jp.fhub.fhub_feeling.controller;

import jp.fhub.fhub_feeling.dto.requestdto.LoginRequestDto;
import jp.fhub.fhub_feeling.dto.requestdto.RegisterRequestDto;
import jp.fhub.fhub_feeling.dto.responsedto.LoginResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.MeResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.RefreshResponseDto;
import jp.fhub.fhub_feeling.entity.User;
import jp.fhub.fhub_feeling.entity.Role;
import jp.fhub.fhub_feeling.exception.customexception.auth.LoginException;
import jp.fhub.fhub_feeling.repository.RoleRepository;
import jp.fhub.fhub_feeling.repository.UserRepository;
import jp.fhub.fhub_feeling.service.JwtBlacklistService;
import jp.fhub.fhub_feeling.service.UserService;
import jp.fhub.fhub_feeling.util.JwtUtil;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String AUTH_HEADER = "Authorization";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtBlacklistService jwtBlacklistService;

    public AuthController(UserRepository userRepository, 
            RoleRepository roleRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder,
            UserService userService, JwtBlacklistService jwtBlacklistService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.jwtBlacklistService = jwtBlacklistService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("パスワードが一致していません");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("既に登録されているメールアドレスです。別のメールアドレスでお試しください。");
        }

        Role role = roleRepository.findByName("user")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = new User();
        user.setRole(role);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        return ResponseEntity.ok("会員登録完了がしました。");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .filter(user -> passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getEmail(), user.getRoles());
                    String roleName = user.getRole() != null ? user.getRole().getName() : null;
                    return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponseDto(token, roleName));
                })
                .orElseThrow(() -> new LoginException("メールアドレスまたはパスワードが間違っています。"));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponseDto> me(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER);
        String token = authHeader.substring(7);

        String email = jwtUtil.validateTokenAndRetrieveSubject(token);

        // トークンがブラックリストに登録されていないか確認
        if (jwtBlacklistService.isTokenBlacklisted(token)) {
            MeResponseDto errorResponse = new MeResponseDto("トークンは無効です。再ログインしてください。");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        User user = userService.findByEmail(email);
        // ユーザー情報をMeResponseDtoにセット
        MeResponseDto response = new MeResponseDto(
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRole().getName());

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponseDto> refresh(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(new RefreshResponseDto("アクセストークンが含まれていません。"));
        }
        String accessToken = authHeader.substring(7);

        try {
            String email = jwtUtil.validateTokenAndRetrieveSubject(accessToken);
            List<String> roles = jwtUtil.extractRoles(accessToken);
            String newRefreshToken = jwtUtil.generateRefreshToken(email, roles);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RefreshResponseDto(newRefreshToken));

        } catch (JWTVerificationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RefreshResponseDto("リフレッシュトークンが無効です。"));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTH_HEADER);
        String accessToken = authHeader.substring(7);

        try {
            jwtUtil.validateTokenAndRetrieveSubject(accessToken);
            jwtBlacklistService.addToBlacklist(accessToken);
            return ResponseEntity.status(HttpStatus.CREATED).body("ログアウト成功");

        } catch (JWTVerificationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("無効なトークンです");
        }
    }
}
