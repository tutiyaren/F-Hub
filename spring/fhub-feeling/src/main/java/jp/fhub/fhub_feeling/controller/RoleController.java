package jp.fhub.fhub_feeling.controller;

import jp.fhub.fhub_feeling.dto.responsedto.TopPageResponseDto;
import jp.fhub.fhub_feeling.service.TopPageService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/")
public class RoleController {

    private final TopPageService topPageService;

    public RoleController(TopPageService topPageService) {
        this.topPageService = topPageService;
    }

    @GetMapping("")
    public ResponseEntity<TopPageResponseDto> index(HttpServletRequest request)
    {
        TopPageResponseDto topPageResponseDto = topPageService.getTopPage(request);
        return ResponseEntity.ok().body(topPageResponseDto);
    }

}
