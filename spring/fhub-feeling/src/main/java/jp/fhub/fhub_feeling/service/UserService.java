package jp.fhub.fhub_feeling.service;

import jp.fhub.fhub_feeling.dto.requestdto.LoginRequestDto;
import jp.fhub.fhub_feeling.dto.requestdto.RegisterRequestDto;
import jp.fhub.fhub_feeling.dto.responsedto.LoginResponseDto;
import jp.fhub.fhub_feeling.entity.Role;
import jp.fhub.fhub_feeling.entity.User;
import jp.fhub.fhub_feeling.repository.UserRepository;
import jp.fhub.fhub_feeling.util.JwtUtil;
import jp.fhub.fhub_feeling.exception.customexception.auth.LoginException;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ValidationService validationService;

    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, ValidationService validationService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.validationService = validationService;
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
}
