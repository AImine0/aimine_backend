package com.aimine.aimine.keyword.controller;

import com.aimine.aimine.common.dto.ApiResponse;
import com.aimine.aimine.keyword.dto.KeywordByTypeResponse;
import com.aimine.aimine.keyword.dto.KeywordListResponse;
import com.aimine.aimine.keyword.dto.KeywordServiceListResponse;
import com.aimine.aimine.keyword.service.KeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "키워드 관리", description = "키워드 관련 API")
@RestController
@RequestMapping("/keywords")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @Operation(summary = "키워드 목록 조회", description = "모든 키워드 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<KeywordListResponse>> getAllKeywords() {
        log.info("키워드 전체 목록 조회 요청");

        KeywordListResponse response = keywordService.getAllKeywords();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "키워드 타입별 조회", description = "특정 타입의 키워드 목록을 조회합니다.")
    @GetMapping(params = "type")
    public ResponseEntity<ApiResponse<KeywordByTypeResponse>> getKeywordsByType(
            @Parameter(description = "키워드 타입 (FEATURE, FUNCTION, INDUSTRY, USE_CASE)", required = true)
            @RequestParam String type
    ) {
        log.info("키워드 타입별 조회 요청: type={}", type);

        KeywordByTypeResponse response = keywordService.getKeywordsByType(type);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "특정 키워드의 AI 서비스 목록 조회",
            description = "특정 키워드를 가진 AI 서비스 목록을 조회합니다.")
    @GetMapping("/{keywordId}/aiservices")
    public ResponseEntity<ApiResponse<KeywordServiceListResponse>> getAiServicesByKeyword(
            @Parameter(description = "키워드 ID") @PathVariable Long keywordId
    ) {
        log.info("키워드별 AI 서비스 목록 조회 요청: keywordId={}", keywordId);

        KeywordServiceListResponse response = keywordService.getAiServicesByKeyword(keywordId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}