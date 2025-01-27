package jp.fhub.fhub_feeling.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import jp.fhub.fhub_feeling.dto.responsedto.TopPageResponseDto;
import jp.fhub.fhub_feeling.entity.Diary;
import jp.fhub.fhub_feeling.entity.User;
import jp.fhub.fhub_feeling.repository.DiaryRepository;
import jp.fhub.fhub_feeling.repository.UserRepository;
import jp.fhub.fhub_feeling.util.JwtUtil;

@Service
public class TopPageService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final RoleService roleService;
    private final CustomUserDetailsService customUserDetailsService;

    public TopPageService(
        JwtUtil jwtUtil,
        UserRepository userRepository,
        DiaryRepository diaryRepository,
        RoleService roleService,
        CustomUserDetailsService customUserDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.diaryRepository = diaryRepository;
        this.roleService = roleService;
        this.customUserDetailsService = customUserDetailsService;
    }
    
    public List<TopPageResponseDto> getTopPage(HttpServletRequest request) {
        String token = customUserDetailsService.getLogunUserTokenHeader(request);
        String email = jwtUtil.validateTokenAndRetrieveSubject(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
        String roleName = user.getRole() != null ? user.getRole().getName() : null;

        if (roleService.isUserRole(roleName)) {
            List<Diary> diaries = diaryRepository.findTop3ByUserOrderByCreatedAtDesc(user);
            return diaries.stream().map(TopPageResponseDto::fromEntity).toList();
        }

        if (roleService.isHospitalAdminRole(roleName)) {
            List<UUID> hospitalIds = roleService.getHospitalIds(user);
            if (hospitalIds.isEmpty()) {
                return Collections.emptyList();
            }

            List<Diary> diaries = diaryRepository.findTop3ByUser_HospitalUsers_Hospital_IdOrderByCreatedAtDesc(hospitalIds.get(0));
            return diaries.stream().map(TopPageResponseDto::fromEntity).toList();
        }
        
        if (roleService.isSystemAdminRole(roleName)) {
            List<Diary> diaries = diaryRepository.findTop3ByOrderByCreatedAtDesc();
            return diaries.stream().map(TopPageResponseDto::fromEntity).toList();
        }

        return Collections.emptyList();
    }
}
