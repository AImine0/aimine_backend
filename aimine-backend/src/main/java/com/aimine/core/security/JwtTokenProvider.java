package com.aimine.core.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long validityMs = 1000L * 60 * 60;

    public String createToken(String sub) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + validityMs);
        return Jwts.builder().setSubject(sub).setIssuedAt(now).setExpiration(exp).signWith(key).compact();
    }
}
