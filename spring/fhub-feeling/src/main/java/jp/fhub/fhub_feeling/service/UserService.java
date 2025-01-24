package jp.fhub.fhub_feeling.service;

import jp.fhub.fhub_feeling.dto.requestdto.LoginRequestDto;
import jp.fhub.fhub_feeling.dto.requestdto.RegisterRequestDto;
import jp.fhub.fhub_feeling.dto.responsedto.LoginResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.LogoutResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.MeResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.RefreshResponseDto;
import jp.fhub.fhub_feeling.entity.Role;
import jp.fhub.fhub_feeling.entity.User;
import jp.fhub.fhub_feeling.repository.UserRepository;
import jp.fhub.fhub_feeling.util.JwtUtil;
import jp.fhub.fhub_feeling.exception.customexception.auth.LoginException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.exceptions.JWTVerificationException;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ValidationService validationService;
    private final JwtBlacklistService jwtBlacklistService;
    private final CustomUserDetailsService customUserDetailsService;

    public UserService(
        UserRepository userRepository,
        RoleService roleService,
        PasswordEncoder passwordEncoder,
        JwtUtil jwtUtil,
        ValidationService validationService, 
        JwtBlacklistService jwtBlacklistService,
        CustomUserDetailsService customUserDetailsService
    ) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.validationService = validationService;
        this.jwtBlacklistService = jwtBlacklistService;
        this.customUserDetailsService = customUserDetailsService;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void registerUser(RegisterRequestDto request) {
        validationService.validateRegistrationRequest(request);

        Role role = roleService.getRoleByName("user");

        User user = new User();
        user.setRole(role);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    public LoginResponseDto loginUser(LoginRequestDto loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new LoginException("メールアドレスまたはパスワードが間違っています"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new LoginException("メールアドレスまたはパスワードが間違っています");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRoles());
        String roleName = user.getRole() != null ? user.getRole().getName() : null;

        return new LoginResponseDto(token, roleName);
    }

    public MeResponseDto meUser(HttpServletRequest request) {
        String token = customUserDetailsService.getLogunUserTokenHeader(request);
        String email = jwtUtil.validateTokenAndRetrieveSubject(token);
        if (jwtBlacklistService.isTokenBlacklisted(token)) {
            throw new IllegalArgumentException("トークンは無効です。再ログインしてください。");
        }
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
        return new MeResponseDto(
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole().getName());
    }

    public RefreshResponseDto refreshUser(HttpServletRequest request) {
        String accessToken = customUserDetailsService.getLogunUserTokenHeader(request);
        try {
            String email = jwtUtil.validateTokenAndRetrieveSubject(accessToken);
            List<String> roles = jwtUtil.extractRoles(accessToken);
            String newRefreshToken = jwtUtil.generateRefreshToken(email, roles);
            return new RefreshResponseDto(newRefreshToken);

        } catch (JWTVerificationException e) {
            return new RefreshResponseDto("リフレッシュトークンが無効です。再ログインしてください。");
        }
    }

    public LogoutResponseDto logoutUser(HttpServletRequest request) {
        String accessToken = customUserDetailsService.getLogunUserTokenHeader(request);
        try {
            jwtUtil.validateTokenAndRetrieveSubject(accessToken);
            jwtBlacklistService.addToBlacklist(accessToken);
            return new LogoutResponseDto("ログアウト成功");

        } catch (JWTVerificationException e) {
            return new LogoutResponseDto("無効なトークンです");
        }
    }
}
