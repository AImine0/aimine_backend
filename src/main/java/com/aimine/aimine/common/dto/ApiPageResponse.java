package com.aimine.aimine.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiPageResponse<T> {
    private boolean success;
    private String message;
    private List<T> data;
    private PageInfo pageInfo;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    // 성공 응답 (페이징 데이터)
    public static <T> ApiPageResponse<T> success(List<T> data, PageInfo pageInfo) {
        return ApiPageResponse.<T>builder()
                .success(true)
                .data(data)
                .pageInfo(pageInfo)
                .build();
    }

    // 성공 응답 (메시지 + 페이징 데이터)
    public static <T> ApiPageResponse<T> success(String message, List<T> data, PageInfo pageInfo) {
        return ApiPageResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .pageInfo(pageInfo)
                .build();
    }
}