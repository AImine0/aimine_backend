package com.aimine.aimine.search.service;

import com.aimine.aimine.search.domain.SearchHistory;
import com.aimine.aimine.search.repository.SearchHistoryRepository;
import com.aimine.aimine.user.domain.User;
import com.aimine.aimine.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserService userService;

    private static final int MAX_HISTORY_COUNT = 10;
    private static final int MIN_QUERY_LENGTH = 2;

    /**
     * 검색어 저장
     */
    @Transactional
    public void saveSearchQuery(Long userId, String query) {
        if (!isValidSearchQuery(query) || userId == null) {
            log.debug("검색어 저장 조건 미충족 - userId: {}, query: {}", userId, query);
            return;
        }

        String cleanedQuery = cleanQuery(query);
        log.debug("검색어 저장 시도 - userId: {}, query: {}", userId, cleanedQuery);

        try {
            User user = userService.findById(userId);

            // 기존 동일한 검색어가 있으면 삭제 (최신 순서로 정렬하기 위해)
            searchHistoryRepository.findByUserIdAndSearchQuery(userId, cleanedQuery)
                    .ifPresent(existing -> {
                        log.debug("기존 검색어 삭제 - userId: {}, query: {}", userId, cleanedQuery);
                        searchHistoryRepository.delete(existing);
                    });

            // 새 검색어 저장
            SearchHistory searchHistory = SearchHistory.builder()
                    .user(user)
                    .searchQuery(cleanedQuery)
                    .build();

            searchHistoryRepository.save(searchHistory);
            log.info("검색어 저장 완료 - userId: {}, query: {}", userId, cleanedQuery);

        } catch (Exception e) {
            log.error("검색어 저장 실패 - userId: {}, query: {}, error: {}", userId, cleanedQuery, e.getMessage());
        }
    }

    /**
     * 사용자별 최근 검색어 목록 조회
     */
    public List<String> getRecentSearchQueries(Long userId) {
        if (userId == null) {
            log.debug("사용자 ID가 null입니다.");
            return List.of();
        }

        try {
            List<String> queries = searchHistoryRepository.findRecentSearchQueriesByUserId(
                    userId,
                    PageRequest.of(0, MAX_HISTORY_COUNT)
            );
            log.debug("최근 검색어 조회 완료 - userId: {}, count: {}", userId, queries.size());
            return queries;

        } catch (Exception e) {
            log.error("최근 검색어 조회 실패 - userId: {}, error: {}", userId, e.getMessage());
            return List.of();
        }
    }

    /**
     * 특정 검색어 삭제
     */
    @Transactional
    public void deleteSearchQuery(Long userId, String query) {
        if (!StringUtils.hasText(query) || userId == null) {
            log.debug("검색어 삭제 조건 미충족 - userId: {}, query: {}", userId, query);
            return;
        }

        try {
            String cleanedQuery = cleanQuery(query);
            searchHistoryRepository.deleteByUserIdAndSearchQuery(userId, cleanedQuery);
            log.info("검색어 삭제 완료 - userId: {}, query: {}", userId, cleanedQuery);

        } catch (Exception e) {
            log.error("검색어 삭제 실패 - userId: {}, query: {}, error: {}", userId, query, e.getMessage());
        }
    }

    /**
     * 모든 검색어 이력 삭제
     */
    @Transactional
    public void deleteAllSearchHistory(Long userId) {
        if (userId == null) {
            log.debug("사용자 ID가 null입니다.");
            return;
        }

        try {
            searchHistoryRepository.deleteAllByUserId(userId);
            log.info("모든 검색어 이력 삭제 완료 - userId: {}", userId);

        } catch (Exception e) {
            log.error("모든 검색어 이력 삭제 실패 - userId: {}, error: {}", userId, e.getMessage());
        }
    }

    /**
     * 사용자별 검색어 개수 조회
     */
    public Long getSearchHistoryCount(Long userId) {
        if (userId == null) {
            return 0L;
        }

        try {
            return searchHistoryRepository.countDistinctByUserId(userId);
        } catch (Exception e) {
            log.error("검색어 개수 조회 실패 - userId: {}, error: {}", userId, e.getMessage());
            return 0L;
        }
    }

    /**
     * 검색어 유효성 검사
     */
    private boolean isValidSearchQuery(String query) {
        return StringUtils.hasText(query) && query.trim().length() >= MIN_QUERY_LENGTH;
    }

    /**
     * 검색어 정리 (공백 제거, 길이 제한)
     */
    private String cleanQuery(String query) {
        String cleaned = query.trim();
        // 500자 제한 (DB 컬럼 크기에 맞춤)
        if (cleaned.length() > 500) {
            cleaned = cleaned.substring(0, 500);
        }
        return cleaned;
    }
}