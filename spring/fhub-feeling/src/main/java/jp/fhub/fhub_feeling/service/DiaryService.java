package jp.fhub.fhub_feeling.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jp.fhub.fhub_feeling.dto.responsedto.DiaryResponseDto;
import jp.fhub.fhub_feeling.entity.Diary;
import jp.fhub.fhub_feeling.entity.Hospital;
import jp.fhub.fhub_feeling.entity.User;
import jp.fhub.fhub_feeling.repository.DiaryRepository;
import jp.fhub.fhub_feeling.repository.UserRepository;
import jp.fhub.fhub_feeling.util.JwtUtil;
import jp.fhub.fhub_feeling.entity.HospitalUser;

@Service
public class DiaryService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public DiaryService(
        JwtUtil jwtUtil,
        UserRepository userRepository,
        DiaryRepository diaryRepository,
        CustomUserDetailsService customUserDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.diaryRepository = diaryRepository;
        this.customUserDetailsService = customUserDetailsService;
    }

    public List<DiaryResponseDto> getDiaryList(HttpServletRequest request) {
        String token = customUserDetailsService.getLogunUserTokenHeader(request);
        String email = jwtUtil.validateTokenAndRetrieveSubject(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
            
        String roleName = user.getRole() != null ? user.getRole().getName() : null;

        if ("user".equals(roleName)) {
            List<Diary> diaries = diaryRepository.findByUserOrderByCreatedAtDesc(user);
            return diaries.stream().map(DiaryResponseDto::fromEntity).toList();
        }

        if ("hospital_admin".equals(roleName)) {
            List<UUID> hospitalIds = user.getHospitalUsers().stream()
                    .map(HospitalUser::getHospital)
                    .filter(Objects::nonNull)
                    .map(Hospital::getId)
                    .distinct()
                    .toList();
            
            if (hospitalIds.isEmpty()) {
                return Collections.emptyList();
            }

            List<Diary> diaries = diaryRepository.findByUser_hospitalUsers_Hospital_IdOrderByCreatedAtDesc(hospitalIds.get(0));
            return diaries.stream().map(DiaryResponseDto::fromEntity).toList();
        }

        if ("system_admin".equals(roleName)) {
            List<Diary> diaries = diaryRepository.findByOrderByCreatedAtDesc();
            return diaries.stream().map(DiaryResponseDto::fromEntity).toList();
        }
        return Collections.emptyList();
    }
}
