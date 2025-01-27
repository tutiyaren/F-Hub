package jp.fhub.fhub_feeling.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jp.fhub.fhub_feeling.dto.responsedto.DiaryResponseDto;
import jp.fhub.fhub_feeling.service.DiaryService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(
        DiaryService diaryService
    ) {
        this.diaryService = diaryService;
    }
    
    @GetMapping("")
    public ResponseEntity<List<DiaryResponseDto>> list(HttpServletRequest request) {
        List<DiaryResponseDto> diaryResponseDto = diaryService.getDiaryList(request);
        return ResponseEntity.ok().body(diaryResponseDto);

    }
}
