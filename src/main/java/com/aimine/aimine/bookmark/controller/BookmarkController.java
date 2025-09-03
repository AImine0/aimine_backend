package com.aimine.aimine.bookmark.controller;

import com.aimine.aimine.bookmark.dto.BookmarkCreateRequest;
import com.aimine.aimine.bookmark.dto.BookmarkCreateResponse;
import com.aimine.aimine.bookmark.dto.BookmarkDeleteResponse;
import com.aimine.aimine.bookmark.dto.BookmarkListResponse;
import com.aimine.aimine.bookmark.service.BookmarkService;
import com.aimine.aimine.common.dto.ApiResponse;
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
@Tag(name = "북마크 관리", description = "북마크 관련 API")
@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
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

    @Operation(summary = "북마크 추가",
            description = "AI 서비스를 북마크에 추가합니다. (인증 필요)")
    @PostMapping
    public ResponseEntity<ApiResponse<BookmarkCreateResponse>> createBookmark(
            @Valid @RequestBody BookmarkCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        // JWT 토큰에서 사용자 ID 추출 (필수)
        Long userId = extractUserIdFromToken(httpRequest);
        log.info("북마크 추가 요청: userId={}, request={}", userId, request.getAiServiceId());

        BookmarkCreateResponse response = bookmarkService.createBookmark(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("북마크가 추가되었습니다", response));
    }

    @Operation(summary = "북마크 제거",
            description = "AI 서비스를 북마크에서 제거합니다. (인증 필요)")
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<ApiResponse<BookmarkDeleteResponse>> deleteBookmark(
            @Parameter(description = "AI 서비스 ID") @PathVariable Long serviceId,
            HttpServletRequest httpRequest
    ) {
        // JWT 토큰에서 사용자 ID 추출 (필수)
        Long userId = extractUserIdFromToken(httpRequest);
        log.info("북마크 제거 요청: userId={}, serviceId={}", userId, serviceId);

        BookmarkDeleteResponse response = bookmarkService.deleteBookmark(userId, serviceId);

        return ResponseEntity.ok(ApiResponse.success("북마크가 제거되었습니다", response));
    }

    @Operation(summary = "내 북마크 목록 조회",
            description = "사용자의 북마크 목록을 조회합니다. (인증 필요)")
    @GetMapping
    public ResponseEntity<ApiResponse<BookmarkListResponse>> getMyBookmarks(
            HttpServletRequest httpRequest
    ) {
        // JWT 토큰에서 사용자 ID 추출 (필수)
        Long userId = extractUserIdFromToken(httpRequest);
        log.info("북마크 목록 조회 요청: userId={}", userId);

        BookmarkListResponse response = bookmarkService.getMyBookmarks(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "북마크 상태 확인",
            description = "특정 AI 서비스의 북마크 여부를 확인합니다. (인증 선택사항)")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Boolean>> checkBookmarkStatus(
            @Parameter(description = "AI 서비스 ID") @RequestParam Long serviceId,
            HttpServletRequest httpRequest
    ) {
        // JWT 토큰에서 사용자 ID 추출 (선택적)
        Long userId = extractUserIdFromTokenOptional(httpRequest);

        if (userId == null) {
            // 인증되지 않은 사용자는 false 반환 (북마크 안됨)
            log.info("비로그인 사용자의 북마크 상태 확인: serviceId={}, result=false", serviceId);
            return ResponseEntity.ok(ApiResponse.success(false));
        }

        log.info("북마크 상태 확인 요청: userId={}, serviceId={}", userId, serviceId);

        boolean isBookmarked = bookmarkService.isBookmarked(userId, serviceId);

        return ResponseEntity.ok(ApiResponse.success(isBookmarked));
    }
}