package jp.fhub.fhub_feeling.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import jp.fhub.fhub_feeling.constant.UserConstants;
import jp.fhub.fhub_feeling.dto.responsedto.UserResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.UserShowDiaryResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.UserShowResponseDto;
import jp.fhub.fhub_feeling.entity.Diary;
import jp.fhub.fhub_feeling.entity.Hospital;
import jp.fhub.fhub_feeling.entity.HospitalUser;
import jp.fhub.fhub_feeling.entity.User;
import jp.fhub.fhub_feeling.repository.DiaryRepository;
import jp.fhub.fhub_feeling.repository.HospitalUserRepository;
import jp.fhub.fhub_feeling.repository.UserRepository;

@Service
public class UserOperationService {

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final HospitalUserRepository hospitalUserRepository;
    private final RoleService roleService;
    private final UserService userService;

    public UserOperationService(
        UserRepository userRepository,
        DiaryRepository diaryRepository,
        HospitalUserRepository hospitalUserRepository,
        RoleService roleService,
        UserService userService
    ) {
        this.userRepository = userRepository;
        this.diaryRepository = diaryRepository;
        this.hospitalUserRepository = hospitalUserRepository;
        this.roleService = roleService;
        this.userService = userService;
    }

    public List<UserResponseDto> getUserList() {
        User user = userService.authUser();
        String roleName = user.getRole() != null ? user.getRole().getName() : null;

        if (roleService.isUserRole(roleName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, UserConstants.USER_FORBIDDEN);
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

    public UserShowResponseDto showUser(UUID userId) {
        User authUser = userService.authUser();
        String roleName = authUser.getRole() != null ? authUser.getRole().getName() : null;

        if (roleService.isUserRole(roleName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, UserConstants.USER_FORBIDDEN);
        }

        if (authUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, UserConstants.USER_FORBIDDEN);
        }

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, UserConstants.USER_NOT_FOUND));

        if (roleService.isHospitalAdminRole(roleName)) {
            validateHospitalAdminAccess(authUser, user);
        }

        if (roleService.isSystemAdminRole(user.getRole().getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, UserConstants.USER_FORBIDDEN);
        }

        return buildUserShowResponseDto(user);
    }

    private void validateHospitalAdminAccess(User authUser, User targetUser) {
        HospitalUser authHospitalUser = hospitalUserRepository.findHospitalByUserId(authUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, UserConstants.USER_FORBIDDEN));

        HospitalUser targetHospitalUser = hospitalUserRepository.findHospitalByUserId(targetUser.getId()).orElse(null);

        if (targetHospitalUser == null || !authHospitalUser.getHospital().getId().equals(targetHospitalUser.getHospital().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, UserConstants.USER_FORBIDDEN);
        }
    }

    private UserShowResponseDto buildUserShowResponseDto(User user) {
        List<Diary> recentDiaries = diaryRepository.findTop3ByUserIdOrderByCreatedAtDesc(user.getId());
        int totalCount = diaryRepository.countByUserId(user.getId());
        int streakCount = calculateStreak(user);

        HospitalUser hospitalUser = hospitalUserRepository.findHospitalByUserId(user.getId()).orElse(null);
        Hospital hospital = hospitalUser != null ? hospitalUser.getHospital() : null;

        return createUserShowResponseDto(
                user,
                recentDiaries,
                user.getRole().getName(),
                hospital,
                streakCount,
                totalCount);
    }
    
    private static int calculateStreak(User user) {
        List<Diary> diaries = user.getDiaries();
        if (diaries.isEmpty()) return 0;
        diaries.sort(Comparator.comparing(Diary::getCreatedAt).reversed());
        LocalDate today = LocalDate.now();
        int streak = 0;
        for (Diary diary : diaries) {
            LocalDate diaryDate = diary.getCreatedAt().toLocalDate();
            if (streak == 0) {
                if (!diaryDate.isEqual(today.minusDays(1))) {
                    return streak; 
                }
                if (!diaryDate.isEqual(today)) {
                    streak = 1; 
                }
            } else { 
                if (!diaryDate.isEqual(today.minusDays((long) streak + 1))) {
                    return streak;
                }
                streak++;
            }
        }
        return streak;
    }

    private UserShowResponseDto createUserShowResponseDto(
        User user,
        List<Diary> diaries,
        String roleName,
        Hospital hospital,
        int streakCount,
        int totalCount
    ) {
        List<UserShowDiaryResponseDto> userShowDiaryResponses = diaries.stream().map(diary ->
            new UserShowDiaryResponseDto(
                diary.getId(),
                diary.getMoodScore(),
                diary.getGoodContents(),
                diary.getContents(),
                diary.getCreatedAt()
            )
        ).toList();
        
        return new UserShowResponseDto(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            userShowDiaryResponses,
            roleName,
            hospital != null ? hospital.getId() : null,
            hospital != null ? hospital.getName() : null,
            streakCount,
            totalCount
        );
    }
}
