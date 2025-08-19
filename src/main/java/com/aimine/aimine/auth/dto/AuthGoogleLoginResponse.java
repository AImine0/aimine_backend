package com.aimine.aimine.auth.dto;

import com.aimine.aimine.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthGoogleLoginResponse {
    private boolean success;
    private String message;
    private String accessToken;
    private UserInfo user;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String googleId;
        private String email;
        private String name;

        public static UserInfo from(User user) {
            return UserInfo.builder()
                    .id(user.getId())
                    .googleId(user.getGoogleId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .build();
        }
    }

    public static AuthGoogleLoginResponse success(String accessToken, User user) {
        return AuthGoogleLoginResponse.builder()
                .success(true)
                .message("구글 로그인 성공")
                .accessToken(accessToken)
                .user(UserInfo.from(user))
                .build();
    }
}