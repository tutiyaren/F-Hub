package jp.fhub.fhub_feeling.service;

import jp.fhub.fhub_feeling.dto.requestdto.RegisterRequestDto;
import jp.fhub.fhub_feeling.entity.Role;
import jp.fhub.fhub_feeling.entity.User;
import jp.fhub.fhub_feeling.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void registerUser(RegisterRequestDto request) {
        validateRegistrationRequest(request);

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

    private void validateRegistrationRequest(RegisterRequestDto request) {
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();
        boolean isEqualsCheckPassword = password.equals(confirmPassword);
        if (!isEqualsCheckPassword) {
            throw new IllegalArgumentException("パスワードが一致していません");
        }

        boolean isEmailAlreadyRegistered = userRepository.findByEmail(request.getEmail()).isPresent();
        if (isEmailAlreadyRegistered) {
            throw new IllegalArgumentException("既に登録されているメールアドレスです。別のメールアドレスでお試しください。");
        }
    }
}
