package com.aimine.aimine.aicombination.controller;

import com.aimine.aimine.aicombination.dto.AiCombinationDetailResponse;
import com.aimine.aimine.aicombination.dto.AiCombinationListResponse;
import com.aimine.aimine.aicombination.service.AiCombinationService;
import com.aimine.aimine.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "AI 조합 추천", description = "AI 조합 추천 관련 API")
@RestController
@RequestMapping("/ai-combinations")
@RequiredArgsConstructor
public class AiCombinationController {

    private final AiCombinationService aiCombinationService;

    @Operation(summary = "AI 조합 목록 조회",
            description = "AI 조합 목록을 카테고리별, featured 조건으로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<AiCombinationListResponse>> getAiCombinations(
            @Parameter(description = "카테고리명", example = "디자인")
            @RequestParam(required = false) String category,

            @Parameter(description = "추천 조합 여부", example = "true")
            @RequestParam(required = false) Boolean featured
    ) {
        log.info("AI 조합 목록 조회 요청: category={}, featured={}", category, featured);

        AiCombinationListResponse response = aiCombinationService.getAiCombinations(category, featured);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "AI 조합 상세 조회",
            description = "특정 AI 조합의 상세 정보를 조회합니다.")
    @GetMapping("/{combinationId}")
    public ResponseEntity<ApiResponse<AiCombinationDetailResponse>> getAiCombinationDetail(
            @Parameter(description = "AI 조합 ID") @PathVariable Long combinationId
    ) {
        log.info("AI 조합 상세 조회 요청: combinationId={}", combinationId);

        AiCombinationDetailResponse response = aiCombinationService.getAiCombinationDetail(combinationId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "AI 조합 카테고리 목록 조회",
            description = "AI 조합의 카테고리 목록을 조회합니다.")
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<java.util.List<String>>> getCategories() {
        log.info("AI 조합 카테고리 목록 조회 요청");

        java.util.List<String> categories = aiCombinationService.getCategories();

        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}