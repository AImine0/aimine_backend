package com.aimine.aimine.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryResponse {

    /**
     * 최근 검색어 목록
     */
    private List<String> recentQueries;

    /**
     * 총 검색어 개수 (중복 제거)
     */
    private Long totalCount;

    /**
     * 성공 여부
     */
    @Builder.Default
    private Boolean success = true;

    /**
     * 메시지
     */
    private String message;

    /**
     * 간단한 응답 생성 (검색어 목록만)
     */
    public static SearchHistoryResponse of(List<String> recentQueries) {
        return SearchHistoryResponse.builder()
                .recentQueries(recentQueries)
                .totalCount((long) recentQueries.size())
                .success(true)
                .build();
    }

    /**
     * 상세 응답 생성 (검색어 목록 + 총 개수)
     */
    public static SearchHistoryResponse of(List<String> recentQueries, Long totalCount) {
        return SearchHistoryResponse.builder()
                .recentQueries(recentQueries)
                .totalCount(totalCount)
                .success(true)
                .build();
    }

    /**
     * 성공 응답 생성 (메시지 포함)
     */
    public static SearchHistoryResponse success(String message) {
        return SearchHistoryResponse.builder()
                .success(true)
                .message(message)
                .build();
    }

    /**
     * 실패 응답 생성
     */
    public static SearchHistoryResponse failure(String message) {
        return SearchHistoryResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}