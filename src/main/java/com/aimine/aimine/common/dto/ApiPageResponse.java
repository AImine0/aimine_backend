package com.aimine.aimine.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiPageResponse<T> {
    private boolean success;
    private String message;
    private T data; // 제네릭 타입으로 수정
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

    // ==================== 기존 메소드들 ====================

    // 성공 응답 (페이징 데이터)
    public static <T> ApiPageResponse<List<T>> success(List<T> data, PageInfo pageInfo) {
        return ApiPageResponse.<List<T>>builder()
                .success(true)
                .data(data)
                .pageInfo(pageInfo)
                .build();
    }

    // 성공 응답 (메시지 + 페이징 데이터)
    public static <T> ApiPageResponse<List<T>> success(String message, List<T> data, PageInfo pageInfo) {
        return ApiPageResponse.<List<T>>builder()
                .success(true)
                .message(message)
                .data(data)
                .pageInfo(pageInfo)
                .build();
    }

    // ==================== 컴파일 에러 해결을 위한 추가 메소드들 ====================

    /**
     * Spring Data Page 객체와 응답 데이터로부터 ApiPageResponse 생성
     */
    public static <T, R> ApiPageResponse<T> of(T responseData, Page<R> page) {
        PageInfo pageInfo = PageInfo.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();

        return ApiPageResponse.<T>builder()
                .success(true)
                .data(responseData)
                .pageInfo(pageInfo)
                .build();
    }

    /**
     * 메시지와 함께 ApiPageResponse 생성
     */
    public static <T, R> ApiPageResponse<T> of(String message, T responseData, Page<R> page) {
        PageInfo pageInfo = PageInfo.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();

        return ApiPageResponse.<T>builder()
                .success(true)
                .message(message)
                .data(responseData)
                .pageInfo(pageInfo)
                .build();
    }

    /**
     * 성공 응답 (데이터만)
     */
    public static <T> ApiPageResponse<T> success(T data) {
        return ApiPageResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    /**
     * 성공 응답 (메시지 + 데이터)
     */
    public static <T> ApiPageResponse<T> success(String message, T data) {
        return ApiPageResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * 실패 응답
     */
    public static <T> ApiPageResponse<T> failure(String message) {
        return ApiPageResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}