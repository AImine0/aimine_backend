package com.aimine.aimine.bookmark.controller;

import com.aimine.aimine.bookmark.dto.BookmarkCreateRequest;
import com.aimine.aimine.bookmark.dto.BookmarkCreateResponse;
import com.aimine.aimine.bookmark.dto.BookmarkDeleteResponse;
import com.aimine.aimine.bookmark.dto.BookmarkListResponse;
import com.aimine.aimine.bookmark.service.BookmarkService;
import com.aimine.aimine.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Operation(summary = "북마크 추가",
            description = "AI 서비스를 북마크에 추가합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<BookmarkCreateResponse>> createBookmark(
            @Parameter(description = "사용자 ID (임시)", example = "1")
            @RequestParam(defaultValue = "1") Long userId,
            @Valid @RequestBody BookmarkCreateRequest request
    ) {
        log.info("북마크 추가 요청: userId={}, request={}", userId, request.getAiServiceId());

        BookmarkCreateResponse response = bookmarkService.createBookmark(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("북마크가 추가되었습니다", response));
    }

    @Operation(summary = "북마크 제거",
            description = "AI 서비스를 북마크에서 제거합니다.")
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<ApiResponse<BookmarkDeleteResponse>> deleteBookmark(
            @Parameter(description = "사용자 ID (임시)", example = "1")
            @RequestParam(defaultValue = "1") Long userId,
            @Parameter(description = "AI 서비스 ID") @PathVariable Long serviceId
    ) {
        log.info("북마크 제거 요청: userId={}, serviceId={}", userId, serviceId);

        BookmarkDeleteResponse response = bookmarkService.deleteBookmark(userId, serviceId);

        return ResponseEntity.ok(ApiResponse.success("북마크가 제거되었습니다", response));
    }

    @Operation(summary = "내 북마크 목록 조회",
            description = "사용자의 북마크 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<BookmarkListResponse>> getMyBookmarks(
            @Parameter(description = "사용자 ID (임시)", example = "1")
            @RequestParam(defaultValue = "1") Long userId
    ) {
        log.info("북마크 목록 조회 요청: userId={}", userId);

        BookmarkListResponse response = bookmarkService.getMyBookmarks(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "북마크 상태 확인",
            description = "특정 AI 서비스의 북마크 여부를 확인합니다.")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Boolean>> checkBookmarkStatus(
            @Parameter(description = "사용자 ID (임시)", example = "1")
            @RequestParam(defaultValue = "1") Long userId,
            @Parameter(description = "AI 서비스 ID") @RequestParam Long serviceId
    ) {
        log.info("북마크 상태 확인 요청: userId={}, serviceId={}", userId, serviceId);

        boolean isBookmarked = bookmarkService.isBookmarked(userId, serviceId);

        return ResponseEntity.ok(ApiResponse.success(isBookmarked));
    }
}