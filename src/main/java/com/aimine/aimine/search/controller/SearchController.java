package com.aimine.aimine.search.controller;

import com.aimine.aimine.search.dto.SearchResponse;
import com.aimine.aimine.search.dto.SearchHistoryResponse;
import com.aimine.aimine.search.dto.SearchSuggestionsResponse;
import com.aimine.aimine.search.service.SearchService;
import com.aimine.aimine.search.service.SearchHistoryService;
import com.aimine.aimine.security.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "검색 API")
public class SearchController {

    private final SearchService searchService;
    private final SearchHistoryService searchHistoryService;  // 새로 추가
    private final JwtTokenProvider jwtTokenProvider;          // 새로 추가

    /**
     * 통합 검색 (수정됨: 검색어 저장 로직 추가)
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
            @RequestParam(defaultValue = "20") int size,

            HttpServletRequest request  // 새로 추가
    ) {
        log.info("통합 검색 요청 - q: {}, category: {}, pricing: {}, sort: {}",
                q, category, pricing, sort);

        SearchResponse response = searchService.search(q, category, pricing, sort, page, size);

        // 검색어가 있고 로그인된 사용자인 경우 검색어 저장 (새로 추가)
        if (StringUtils.hasText(q)) {
            Long userId = extractUserIdFromRequest(request);
            if (userId != null) {
                try {
                    searchHistoryService.saveSearchQuery(userId, q);
                } catch (Exception e) {
                    // 검색어 저장 실패해도 검색 결과는 정상 반환
                    log.warn("검색어 저장 실패 - userId: {}, query: {}, error: {}",
                            userId, q, e.getMessage());
                }
            }
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 최근 검색어 목록 조회 (새로 추가)
     */
    @GetMapping("/history")
    @Operation(summary = "최근 검색어 조회", description = "사용자의 최근 검색어 목록을 조회합니다.")
    public ResponseEntity<List<String>> getSearchHistory(HttpServletRequest request) {
        log.info("최근 검색어 조회 요청");

        Long userId = extractUserIdFromRequest(request);
        if (userId == null) {
            log.warn("인증되지 않은 사용자의 검색어 이력 조회 시도");
            return ResponseEntity.ok(List.of()); // 빈 목록 반환
        }

        List<String> recentQueries = searchHistoryService.getRecentSearchQueries(userId);
        log.info("최근 검색어 조회 완료 - userId: {}, count: {}", userId, recentQueries.size());

        return ResponseEntity.ok(recentQueries);
    }

    /**
     * 특정 검색어 삭제 (새로 추가)
     */
    @DeleteMapping("/history")
    @Operation(summary = "검색어 삭제", description = "특정 검색어를 삭제합니다.")
    public ResponseEntity<SearchHistoryResponse> deleteSearchQuery(
            @Parameter(description = "삭제할 검색어", example = "ChatGPT", required = true)
            @RequestParam String query,
            HttpServletRequest request
    ) {
        log.info("검색어 삭제 요청 - query: {}", query);

        Long userId = extractUserIdFromRequest(request);
        if (userId == null) {
            log.warn("인증되지 않은 사용자의 검색어 삭제 시도");
            return ResponseEntity.badRequest()
                    .body(SearchHistoryResponse.failure("로그인이 필요합니다."));
        }

        if (!StringUtils.hasText(query)) {
            return ResponseEntity.badRequest()
                    .body(SearchHistoryResponse.failure("검색어가 필요합니다."));
        }

        searchHistoryService.deleteSearchQuery(userId, query);
        log.info("검색어 삭제 완료 - userId: {}, query: {}", userId, query);

        return ResponseEntity.ok(SearchHistoryResponse.success("검색어가 삭제되었습니다."));
    }

    /**
     * 모든 검색어 이력 삭제 (새로 추가)
     */
    @DeleteMapping("/history/all")
    @Operation(summary = "모든 검색어 삭제", description = "사용자의 모든 검색어 이력을 삭제합니다.")
    public ResponseEntity<SearchHistoryResponse> deleteAllSearchHistory(HttpServletRequest request) {
        log.info("모든 검색어 이력 삭제 요청");

        Long userId = extractUserIdFromRequest(request);
        if (userId == null) {
            log.warn("인증되지 않은 사용자의 전체 검색어 삭제 시도");
            return ResponseEntity.badRequest()
                    .body(SearchHistoryResponse.failure("로그인이 필요합니다."));
        }

        searchHistoryService.deleteAllSearchHistory(userId);
        log.info("모든 검색어 이력 삭제 완료 - userId: {}", userId);

        return ResponseEntity.ok(SearchHistoryResponse.success("모든 검색어 이력이 삭제되었습니다."));
    }

    /**
     * 실시간 연관검색어 조회 (새로 추가)
     */
    @GetMapping("/suggestions")
    @Operation(summary = "실시간 연관검색어", description = "입력된 검색어에 대한 실시간 연관검색어를 제공합니다.")
    public ResponseEntity<SearchSuggestionsResponse> getSearchSuggestions(
            @Parameter(description = "검색어", example = "Chat")
            @RequestParam String q,
            @Parameter(description = "최대 결과 수", example = "10")
            @RequestParam(defaultValue = "10") int limit
    ) {
        log.info("실시간 연관검색어 요청 - q: {}, limit: {}", q, limit);

        SearchSuggestionsResponse suggestions = searchService.getSearchSuggestions(q, limit);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * HTTP 요청에서 JWT 토큰 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * JWT에서 사용자 ID 추출 (새로 추가)
     */
    private Long extractUserIdFromRequest(HttpServletRequest request) {
        try {
            String token = resolveToken(request);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                return jwtTokenProvider.getUserIdFromToken(token);  // 이미 Long을 반환함
            }
        } catch (Exception e) {
            log.debug("JWT 토큰에서 사용자 ID 추출 실패: {}", e.getMessage());
        }
        return null;
    }
}