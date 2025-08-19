package com.aimine.aimine.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthLogoutResponse {
    private boolean success;
    private String message;

    public static AuthLogoutResponse success() {
        return AuthLogoutResponse.builder()
                .success(true)
                .message("로그아웃되었습니다")
                .build();
    }
}