package com.aimine.aimine.aiservice.controller;

import com.aimine.aimine.aiservice.dto.ServiceDetailResponse;
import com.aimine.aimine.aiservice.dto.ServiceListResponse;
import com.aimine.aimine.aiservice.service.AiServiceService;
import com.aimine.aimine.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "AI 서비스 관리", description = "AI 서비스 관련 API")
@RestController
@RequestMapping("/ai-services")
@RequiredArgsConstructor
public class AiServiceController {

    private final AiServiceService aiServiceService;

    @Operation(summary = "AI 서비스 목록 조회",
            description = "AI 서비스 목록을 페이징, 필터링, 정렬 조건으로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<ServiceListResponse>> getAiServices(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "카테고리명", example = "chatbot")
            @RequestParam(required = false) String category,

            @Parameter(description = "검색어", example = "ChatGPT")
            @RequestParam(required = false) String search,

            @Parameter(description = "정렬 기준 (rating, latest, name, recommendation)", example = "rating")
            @RequestParam(defaultValue = "rating") String sort,

            @Parameter(description = "가격 타입 (FREE, FREEMIUM, PAID)", example = "FREEMIUM")
            @RequestParam(required = false) String pricing
    ) {
        log.info("AI 서비스 목록 조회 요청: page={}, size={}, category={}, search={}, sort={}, pricing={}",
                page, size, category, search, sort, pricing);

        ServiceListResponse response = aiServiceService.getAiServices(
                page, size, category, search, sort, pricing
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "AI 서비스 상세 조회",
            description = "특정 AI 서비스의 상세 정보를 조회합니다.")
    @GetMapping("/{serviceId}")
    public ResponseEntity<ApiResponse<ServiceDetailResponse>> getAiServiceDetail(
            @Parameter(description = "AI 서비스 ID") @PathVariable Long serviceId
    ) {
        log.info("AI 서비스 상세 조회 요청: serviceId={}", serviceId);

        ServiceDetailResponse response = aiServiceService.getAiServiceDetail(serviceId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}