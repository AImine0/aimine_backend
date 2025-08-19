package com.aimine.aimine.auth.service;

import com.aimine.aimine.auth.dto.AuthGoogleLoginResponse;
import com.aimine.aimine.auth.dto.AuthLogoutResponse;
import com.aimine.aimine.auth.dto.UserProfileResponse;
import com.aimine.aimine.common.exception.BusinessException;
import com.aimine.aimine.common.exception.errorcode.AuthErrorCode;
import com.aimine.aimine.security.jwt.JwtTokenProvider;
import com.aimine.aimine.security.service.RefreshTokenService;
import com.aimine.aimine.user.domain.User;
import com.aimine.aimine.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * 구글 토큰으로 로그인/회원가입 처리
     */
    @Transactional
    public AuthGoogleLoginResponse processGoogleLogin(String googleToken) {
        // 실제 구현에서는 Google API를 호출하여 토큰 검증
        // 현재는 간단히 시뮬레이션

        // TODO: Google API 호출하여 사용자 정보 조회
        // GoogleTokenVerifier를 사용하여 토큰 검증 후 사용자 정보 추출

        // 임시로 하드코딩된 사용자 정보 (실제로는 Google API 응답)
        String googleId = "temp_google_id";
        String email = "temp@gmail.com";
        String name = "임시 사용자";

        // 사용자 처리 (로그인 또는 회원가입)
        User user = userService.processGoogleLogin(googleId, email, name);

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // Refresh Token 저장
        refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

        log.info("구글 로그인 성공: {}", user.getEmail());

        return AuthGoogleLoginResponse.success(accessToken, user);
    }

    /**
     * 로그아웃 처리
     */
    @Transactional
    public AuthLogoutResponse logout(String accessToken) {
        try {
            // Access Token에서 사용자 ID 추출
            Long userId = jwtTokenProvider.getUserIdFromToken(accessToken);

            // 해당 사용자의 모든 Refresh Token 삭제
            refreshTokenService.deleteAllRefreshTokensByUserId(userId);

            log.info("사용자 로그아웃: userId={}", userId);

            return AuthLogoutResponse.success();

        } catch (Exception e) {
            log.warn("로그아웃 처리 중 오류: {}", e.getMessage());
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 현재 사용자 정보 조회
     */
    public UserProfileResponse getCurrentUser(User user) {
        return UserProfileResponse.from(user);
    }

    /**
     * Refresh Token으로 Access Token 갱신
     */
    @Transactional
    public String refreshAccessToken(String refreshToken) {
        return refreshTokenService.refreshAccessToken(refreshToken);
    }
}