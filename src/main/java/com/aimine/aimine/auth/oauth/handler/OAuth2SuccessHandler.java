package com.aimine.aimine.auth.oauth.handler;

import com.aimine.aimine.auth.oauth.dto.OAuthUserInfo;
import com.aimine.aimine.auth.oauth.dto.OAuthUserInfoFactory;
import com.aimine.aimine.common.config.AppProperties;
import com.aimine.aimine.security.jwt.JwtTokenProvider;
import com.aimine.aimine.security.service.RefreshTokenService;
import com.aimine.aimine.user.domain.User;
import com.aimine.aimine.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final AppProperties appProperties;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        try {
            // OAuth2 사용자 정보 추출
            OAuthUserInfo oAuthUserInfo = OAuthUserInfoFactory.getOAuthUserInfo(
                    "google", // 현재는 구글만 지원
                    oauth2User.getAttributes()
            );

            // 사용자 조회 또는 생성
            User user = userService.processGoogleLogin(
                    oAuthUserInfo.getProviderId(),
                    oAuthUserInfo.getEmail(),
                    oAuthUserInfo.getName()
            );

            // JWT 토큰 생성
            String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

            // Refresh Token 저장
            refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

            // 환경에 따른 프론트엔드 URL로 리다이렉트 (토큰과 함께)
            String frontendUrl = appProperties.getFrontend().getUrl();
            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/callback")
                    .queryParam("accessToken", accessToken)
                    .queryParam("refreshToken", refreshToken)
                    .build().toUriString();

            log.info("OAuth2 로그인 성공, 리다이렉트: {} -> {}", frontendUrl, targetUrl);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            log.error("OAuth2 성공 처리 중 오류 발생", e);

            String frontendUrl = appProperties.getFrontend().getUrl();
            String errorUrl = frontendUrl + "/auth/error";
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }
}