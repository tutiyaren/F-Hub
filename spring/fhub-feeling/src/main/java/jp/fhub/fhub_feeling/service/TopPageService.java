package jp.fhub.fhub_feeling.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import jp.fhub.fhub_feeling.dto.responsedto.DiaryResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.TopPageResponseDto;
import jp.fhub.fhub_feeling.entity.Diary;
import jp.fhub.fhub_feeling.entity.Hospital;
import jp.fhub.fhub_feeling.entity.HospitalUser;
import jp.fhub.fhub_feeling.entity.User;
import jp.fhub.fhub_feeling.repository.DiaryRepository;
import jp.fhub.fhub_feeling.repository.UserRepository;
import jp.fhub.fhub_feeling.util.JwtUtil;

@Service
public class TopPageService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public TopPageService(
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
    
    public TopPageResponseDto getTopPage(HttpServletRequest request) {
        String token = customUserDetailsService.getLogunUserTokenHeader(request);
        String email = jwtUtil.validateTokenAndRetrieveSubject(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
        String roleName = user.getRole() != null ? user.getRole().getName() : null;

        if ("user".equals(roleName)) {
            List<Diary> diaries = diaryRepository.findTop3ByUserOrderByCreatedAtDesc(user);
            return new TopPageResponseDto(user.getLastName(),diaries.stream().map(DiaryResponseDto::fromEntity).toList());
        }

        if ("hospital_admin".equals(roleName)) {
            List<UUID> hospitalIds = user.getHospitalUsers().stream()
                    .map(HospitalUser::getHospital)
                    .filter(Objects::nonNull)
                    .map(Hospital::getId)
                    .distinct()
                    .toList();

            if (hospitalIds.isEmpty()) {
                return new TopPageResponseDto(user.getLastName(), Collections.emptyList());
            }

            List<Diary> diaries = diaryRepository.findTop3ByUser_HospitalUsers_Hospital_IdOrderByCreatedAtDesc(hospitalIds.get(0));
            return new TopPageResponseDto(user.getLastName(),diaries.stream().map(DiaryResponseDto::fromEntity).toList());
        }
        
        if ("system_admin".equals(roleName)) {
            List<Diary> diaries = diaryRepository.findTop3ByOrderByCreatedAtDesc();
            return new TopPageResponseDto(user.getLastName(),
                    diaries.stream().map(DiaryResponseDto::fromEntity).toList());
        }

        return new TopPageResponseDto(user.getLastName(), Collections.emptyList());
    }
}
