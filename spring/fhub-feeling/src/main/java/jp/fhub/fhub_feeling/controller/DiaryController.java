package jp.fhub.fhub_feeling.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jp.fhub.fhub_feeling.dto.requestdto.DiaryRequestDto;
import jp.fhub.fhub_feeling.dto.responsedto.DiaryDestroyResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.DiaryResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.DiaryShowResponseDto;
import jp.fhub.fhub_feeling.dto.responsedto.DiaryStoreResponseDto;
import jp.fhub.fhub_feeling.service.DiaryService;
import jp.fhub.fhub_feeling.service.ValidationService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    private final DiaryService diaryService;
    private final ValidationService validationService;

    public DiaryController(
        DiaryService diaryService,
        ValidationService validationService
    ) {
        this.diaryService = diaryService;
        this.validationService = validationService;
    }
    
    @GetMapping("")
    public ResponseEntity<List<DiaryResponseDto>> list() {
        List<DiaryResponseDto> diaryResponseDto = diaryService.getDiaryList();
        return ResponseEntity.ok().body(diaryResponseDto);
    }

    @PostMapping("")
    public ResponseEntity<DiaryStoreResponseDto> store(@Valid @RequestBody DiaryRequestDto request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = validationService.extractFirstErrorMessage(bindingResult);
            return ResponseEntity.unprocessableEntity().body(new DiaryStoreResponseDto(errorMessage));
        }
        diaryService.storeDiary(request);
        DiaryStoreResponseDto response = new DiaryStoreResponseDto("新しい日記を保存しました。");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("{diaryId}")
    public ResponseEntity<DiaryShowResponseDto> show(@PathVariable UUID diaryId) {
        DiaryShowResponseDto diaryShowResponseDto = diaryService.showDiary(diaryId);
        return ResponseEntity.ok().body(diaryShowResponseDto);
    }

    @DeleteMapping("{diaryId}")
    public ResponseEntity<DiaryDestroyResponseDto> destroy(@PathVariable UUID diaryId) {
        DiaryDestroyResponseDto response = diaryService.destroyDiary(diaryId);
        return ResponseEntity.ok(response);
    }
    
}
