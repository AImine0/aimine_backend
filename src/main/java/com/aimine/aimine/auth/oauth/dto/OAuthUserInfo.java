package com.aimine.aimine.auth.oauth.dto;

public interface OAuthUserInfo {
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
}