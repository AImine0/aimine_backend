package com.aimine.aimine.auth.oauth.handler;

import com.aimine.aimine.common.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AppProperties appProperties;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        log.error("OAuth2 로그인 실패: {}", exception.getMessage());

        String frontendUrl = appProperties.getFrontend().getUrl();
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/error")
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();

        log.info("OAuth2 로그인 실패, 리다이렉트: {}", targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}