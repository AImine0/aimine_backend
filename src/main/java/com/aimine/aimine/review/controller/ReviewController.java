package com.aimine.aimine.review.controller;

import com.aimine.aimine.common.dto.ApiResponse;
import com.aimine.aimine.review.dto.ReviewCreateRequest;
import com.aimine.aimine.review.dto.ReviewCreateResponse;
import com.aimine.aimine.review.dto.ReviewDeleteResponse;
import com.aimine.aimine.review.dto.ReviewListResponse;
import com.aimine.aimine.review.service.ReviewService;
import com.aimine.aimine.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "리뷰/평점 관리", description = "리뷰 관련 API")
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtTokenProvider jwtTokenProvider;

    // JWT 토큰에서 사용자 ID 추출하는 헬퍼 메서드 (선택적)
    private Long extractUserIdFromTokenOptional(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return null; // 토큰이 없으면 null 반환
            }

            String token = authHeader.substring(7);
            return jwtTokenProvider.getUserIdFromToken(token);
        } catch (Exception e) {
            log.warn("JWT 토큰 파싱 실패: {}", e.getMessage());
            return null;
        }
    }

    // JWT 토큰에서 사용자 ID 추출하는 헬퍼 메서드 (필수)
    private Long extractUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
            throw new RuntimeException("인증이 필요합니다.");
        }

        String token = authHeader.substring(7);
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    @Operation(summary = "리뷰 목록 조회",
            description = "AI 서비스별 또는 전체 리뷰 목록을 조회합니다. (인증 불필요)")
    @GetMapping
    public ResponseEntity<ApiResponse<ReviewListResponse>> getReviews(
            @Parameter(description = "AI 서비스 ID (선택사항, 없으면 전체 조회)", example = "1")
            @RequestParam(required = false) Long serviceId
    ) {
        log.info("리뷰 목록 조회 요청: serviceId={}", serviceId);

        ReviewListResponse response = reviewService.getReviews(serviceId);

        return ResponseEntity.ok(ApiResponse.success("리뷰 조회 성공", response));
    }

    @Operation(summary = "리뷰 작성",
            description = "AI 서비스에 대한 리뷰를 작성합니다. (인증 필요)")
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewCreateResponse>> createReview(
            @Valid @RequestBody ReviewCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        // JWT 토큰에서 사용자 ID 추출 (필수)
        Long userId = extractUserIdFromToken(httpRequest);
        log.info("리뷰 작성 요청: userId={}, toolId={}, rating={}",
                userId, request.getToolId(), request.getRating());

        ReviewCreateResponse response = reviewService.createReview(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("리뷰가 작성되었습니다", response));
    }

    @Operation(summary = "리뷰 삭제",
            description = "작성한 리뷰를 삭제합니다. (인증 필요)")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDeleteResponse>> deleteReview(
            @Parameter(description = "리뷰 ID") @PathVariable Long reviewId,
            HttpServletRequest httpRequest
    ) {
        // JWT 토큰에서 사용자 ID 추출 (필수)
        Long userId = extractUserIdFromToken(httpRequest);
        log.info("리뷰 삭제 요청: userId={}, reviewId={}", userId, reviewId);

        ReviewDeleteResponse response = reviewService.deleteReview(userId, reviewId);

        return ResponseEntity.ok(ApiResponse.success("리뷰가 삭제되었습니다", response));
    }

    @Operation(summary = "리뷰 작성 여부 확인",
            description = "사용자가 특정 AI 서비스에 리뷰를 작성했는지 확인합니다. (인증 필요)")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Boolean>> checkReviewStatus(
            @Parameter(description = "AI 서비스 ID") @RequestParam Long serviceId,
            HttpServletRequest httpRequest
    ) {
        // JWT 토큰에서 사용자 ID 추출 (선택적)
        Long userId = extractUserIdFromTokenOptional(httpRequest);

        if (userId == null) {
            // 인증되지 않은 사용자는 false 반환
            return ResponseEntity.ok(ApiResponse.success(false));
        }

        log.info("리뷰 작성 여부 확인 요청: userId={}, serviceId={}", userId, serviceId);

        boolean hasReviewed = reviewService.hasUserReviewed(userId, serviceId);

        return ResponseEntity.ok(ApiResponse.success(hasReviewed));
    }
}