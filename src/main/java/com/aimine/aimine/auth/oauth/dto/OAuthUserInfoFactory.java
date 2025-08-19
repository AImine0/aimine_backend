package com.aimine.aimine.auth.oauth.dto;

import java.util.Map;

public class OAuthUserInfoFactory {

    public static OAuthUserInfo getOAuthUserInfo(String registrationId, Map<String, Object> attributes) {
        switch (registrationId.toLowerCase()) {
            case "google":
                return new GoogleUserInfo(attributes);
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + registrationId);
        }
    }
}