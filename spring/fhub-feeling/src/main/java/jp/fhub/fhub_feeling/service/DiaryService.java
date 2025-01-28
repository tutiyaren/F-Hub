package jp.fhub.fhub_feeling.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import jp.fhub.fhub_feeling.dto.requestdto.DiaryRequestDto;
import jp.fhub.fhub_feeling.dto.responsedto.DiaryResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.DiaryShowResponseDto;
import jp.fhub.fhub_feeling.entity.Diary;
import jp.fhub.fhub_feeling.entity.HospitalUser;
import jp.fhub.fhub_feeling.entity.User;
import jp.fhub.fhub_feeling.repository.DiaryRepository;
import jp.fhub.fhub_feeling.repository.HospitalUserRepository;
import jp.fhub.fhub_feeling.repository.UserRepository;
import jp.fhub.fhub_feeling.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DiaryService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final HospitalUserRepository hospitalUserRepository;
    private final RoleService roleService;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    public DiaryService(
        JwtUtil jwtUtil,
        UserRepository userRepository,
        DiaryRepository diaryRepository,
        HospitalUserRepository hospitalUserRepository,
        RoleService roleService,
        UserService userService,
        CustomUserDetailsService customUserDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.diaryRepository = diaryRepository;
        this.hospitalUserRepository = hospitalUserRepository;
        this.roleService = roleService;
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
    }

    public List<DiaryResponseDto> getDiaryList(HttpServletRequest request) {
        String token = customUserDetailsService.getLogunUserTokenHeader(request);
        String email = jwtUtil.validateTokenAndRetrieveSubject(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
            
        String roleName = user.getRole() != null ? user.getRole().getName() : null;

        if (roleService.isUserRole(roleName)) {
            List<Diary> diaries = diaryRepository.findByUserOrderByCreatedAtDesc(user);
            return diaries.stream().map(DiaryResponseDto::fromEntity).toList();
        }

        if (roleService.isHospitalAdminRole(roleName)) {
            List<UUID> hospitalIds = roleService.getHospitalIds(user);
            if (hospitalIds.isEmpty()) {
                return Collections.emptyList();
            }

            List<Diary> diaries = diaryRepository.findByUser_hospitalUsers_Hospital_IdOrderByCreatedAtDesc(hospitalIds.get(0));
            return diaries.stream().map(DiaryResponseDto::fromEntity).toList();
        }

        if (roleService.isSystemAdminRole(roleName)) {
            List<Diary> diaries = diaryRepository.findByOrderByCreatedAtDesc();
            return diaries.stream().map(DiaryResponseDto::fromEntity).toList();
        }
        return Collections.emptyList();
    }

    public void storeDiary(DiaryRequestDto request) {
        User user = userService.authUser();

        Diary diary = new Diary();
        diary.setUser(user);
        diary.setMoodScore(request.getMoodScore());
        diary.setGoodContents(request.getGoodContents());
        diary.setContents(request.getContents());
        diary.setCreatedAt(LocalDateTime.now());
        diary.setUpdatedAt(LocalDateTime.now());

        diaryRepository.save(diary);
    }

    public DiaryShowResponseDto showDiary(UUID diaryId) {
        User user = userService.authUser();
        String roleName = user.getRole() != null ? user.getRole().getName() : null;
        Diary diary = diaryRepository.findDiaryById(diaryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "対象の日記が存在しません"));
        User diaryUser = diary.getUser();

        if (roleService.isUserRole(roleName)) {
            return checkUserAccess(user, diary, roleName);
        }

        if (roleService.isHospitalAdminRole(roleName)) {
            return checkHospitalAdminAccess(user, diary, diaryUser, roleName);
        }

        if (roleService.isSystemAdminRole(roleName)) {
            return createDiaryShowResponseDto(diary, diaryUser.getFirstName(), diaryUser.getLastName(), roleName);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "対象の日記が存在しません");
    }

    private DiaryShowResponseDto checkUserAccess(User user, Diary diary, String roleName) {
        if (!diary.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "この日記を表示する権限がありません。");
        }
        return createDiaryShowResponseDto(diary, null, null, roleName);
    }

    private DiaryShowResponseDto checkHospitalAdminAccess(User user, Diary diary, User diaryUser, String roleName) {
        Optional<HospitalUser> hospitalUser = hospitalUserRepository.findByUserId(user.getId());
        if (hospitalUser.isEmpty() || !isUserInHospital(diary.getUser(), hospitalUser.get())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "この日記を表示する権限がありません。");
        }
        return createDiaryShowResponseDto(diary, diaryUser.getFirstName(), diaryUser.getLastName(), roleName);
    }
    
    public boolean isUserInHospital(User user, HospitalUser hospitalUser) {
        return user.getHospitalUsers().stream()
                .anyMatch(h -> h.getHospital().getId().equals(hospitalUser.getHospital().getId()));
    }
    
    private DiaryShowResponseDto createDiaryShowResponseDto(Diary diary, String firstName, String lastName, String roleName) {
        return new DiaryShowResponseDto(
            diary.getId(),
            diary.getMoodScore(),
            diary.getGoodContents(),
            diary.getContents(),
            diary.getCreatedAt(),
            firstName,
            lastName,
            roleName
        );
    }
}
