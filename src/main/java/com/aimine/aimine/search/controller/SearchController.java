package com.aimine.aimine.search.controller;

import com.aimine.aimine.search.dto.SearchResponse;
import com.aimine.aimine.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "검색 API")
public class SearchController {

    private final SearchService searchService;

    /**
     * 통합 검색
     */
    @GetMapping
    @Operation(summary = "통합 검색", description = "AI 서비스를 통합 검색합니다.")
    public ResponseEntity<SearchResponse> search(
            @Parameter(description = "검색어", example = "ChatGPT")
            @RequestParam(required = false) String q,

            @Parameter(description = "카테고리", example = "AI 챗봇")
            @RequestParam(required = false) String category,

            @Parameter(description = "가격 타입", example = "FREEMIUM")
            @RequestParam(required = false) String pricing,

            @Parameter(description = "정렬 기준", example = "rating")
            @RequestParam(defaultValue = "rating") String sort,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("통합 검색 요청 - q: {}, category: {}, pricing: {}, sort: {}",
                q, category, pricing, sort);

        SearchResponse response = searchService.search(q, category, pricing, sort, page, size);

        return ResponseEntity.ok(response);
    }
}