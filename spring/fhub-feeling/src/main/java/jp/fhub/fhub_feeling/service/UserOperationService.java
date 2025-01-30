package jp.fhub.fhub_feeling.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import jp.fhub.fhub_feeling.dto.responsedto.UserResponseDto;
import jp.fhub.fhub_feeling.entity.User;
import jp.fhub.fhub_feeling.repository.UserRepository;

@Service
public class UserOperationService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserService userService;

    public UserOperationService(
        UserRepository userRepository,
        RoleService roleService,
        UserService userService
    ) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.userService = userService;
    }

    public List<UserResponseDto> getUserList() {
        User user = userService.authUser();
        String roleName = user.getRole() != null ? user.getRole().getName() : null;

        if (roleService.isUserRole(roleName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "このページにアクセスできません。詳細は管理者にお問い合わせください。");
        }

        if (roleService.isHospitalAdminRole(roleName)) {
            List<UUID> hospitalIds = roleService.getHospitalIds(user);
            if (hospitalIds.isEmpty()) {
                return Collections.emptyList();
            }
            List<User> hospitalUsers = userRepository.findByHospitalUsers_Hospital_IdOrderByCreatedAtDesc(hospitalIds.get(0));
            return hospitalUsers.stream()
                .filter(u -> !u.getId().equals(user.getId()))
                .map(UserResponseDto::fromEntity)
                .toList();
        }

        if (roleService.isSystemAdminRole(roleName)) {
            List<User> users = userRepository.findAllByOrderByCreatedAtDesc();
            return users.stream()
                .filter(u -> !u.getId().equals(user.getId()))
                .map(UserResponseDto::fromEntity)
                .toList();
        }

        return Collections.emptyList();
    }
}
