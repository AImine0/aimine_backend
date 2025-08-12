package com.aimine.core.common.dto;

public record ApiResponse<T>(boolean success, String message, T data) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, null, data);
    }

    public static <T> ApiResponse<T> fail(String msg) {
        return new ApiResponse<>(false, msg, null);
    }
}
