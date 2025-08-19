package com.aimine.aimine.auth.oauth.service;

import com.aimine.aimine.auth.oauth.dto.OAuthUserInfo;
import com.aimine.aimine.auth.oauth.dto.OAuthUserInfoFactory;
import com.aimine.aimine.user.domain.User;
import com.aimine.aimine.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception e) {
            log.error("OAuth2 사용자 처리 중 오류 발생", e);
            throw new OAuth2AuthenticationException("OAuth2 사용자 처리 실패");
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuthUserInfo oAuthUserInfo = OAuthUserInfoFactory.getOAuthUserInfo(
                registrationId,
                oauth2User.getAttributes()
        );

        // 사용자 정보 처리 (회원가입 또는 로그인)
        User user = userService.processGoogleLogin(
                oAuthUserInfo.getProviderId(),
                oAuthUserInfo.getEmail(),
                oAuthUserInfo.getName()
        );

        log.info("OAuth2 로그인 성공: {}", user.getEmail());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oauth2User.getAttributes(),
                "sub" // Google의 경우 'sub'이 primary key
        );
    }
}