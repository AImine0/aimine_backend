package com.aimine.features.auth;

import com.aimine.core.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JwtTokenProvider jwt;

    public AuthService(JwtTokenProvider jwt) {
        this.jwt = jwt;
    }

    public TokenResponse login(LoginRequest req) {
        return new TokenResponse(jwt.createToken(req.email()));
    }

    public TokenResponse googleLogin(GoogleLoginRequest req) {
        return new TokenResponse(jwt.createToken("google:" + req.idToken()));
    }
}
