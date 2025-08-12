package com.aimine.features.auth;

public record GoogleLoginRequest(String idToken, String authorizationCode) {
}