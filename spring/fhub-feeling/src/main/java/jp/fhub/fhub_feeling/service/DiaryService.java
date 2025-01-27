package jp.fhub.fhub_feeling.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import jp.fhub.fhub_feeling.dto.requestdto.DiaryRequestDto;
import jp.fhub.fhub_feeling.dto.responsedto.DiaryResponseDto;
import jp.fhub.fhub_feeling.entity.Diary;
import jp.fhub.fhub_feeling.entity.User;
import jp.fhub.fhub_feeling.repository.DiaryRepository;
import jp.fhub.fhub_feeling.repository.UserRepository;
import jp.fhub.fhub_feeling.util.JwtUtil;

@Service
public class DiaryService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final RoleService roleService;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    public DiaryService(
        JwtUtil jwtUtil,
        UserRepository userRepository,
        DiaryRepository diaryRepository,
        RoleService roleService,
        UserService userService,
        CustomUserDetailsService customUserDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.diaryRepository = diaryRepository;
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
}
