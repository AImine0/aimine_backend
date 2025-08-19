package com.aimine.aimine.security.service;

import com.aimine.aimine.common.exception.BusinessException;
import com.aimine.aimine.common.exception.errorcode.AuthErrorCode;
import com.aimine.aimine.security.domain.RefreshToken;
import com.aimine.aimine.security.jwt.JwtTokenProvider;
import com.aimine.aimine.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Refresh Token 저장
     */
    @Transactional
    public RefreshToken saveRefreshToken(Long userId, String token) {
        // 기존 토큰이 있다면 삭제
        refreshTokenRepository.deleteByUserId(userId);

        LocalDateTime expiresAt = jwtTokenProvider.getRefreshTokenExpiryDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiresAt(expiresAt)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Refresh Token으로 새로운 Access Token 발급
     */
    @Transactional
    public String refreshAccessToken(String refreshToken) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }

        // 2. DB에서 토큰 조회
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_TOKEN));

        // 3. 만료 여부 확인
        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new BusinessException(AuthErrorCode.EXPIRED_TOKEN);
        }

        // 4. 새로운 Access Token 발급
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        return jwtTokenProvider.generateAccessToken(userId, email);
    }

    /**
     * Refresh Token 삭제 (로그아웃)
     */
    @Transactional
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    /**
     * 사용자의 모든 Refresh Token 삭제
     */
    @Transactional
    public void deleteAllRefreshTokensByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    /**
     * 만료된 토큰 정리
     */
    @Transactional
    public void cleanExpiredTokens() {
        refreshTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        log.info("만료된 refresh token들이 정리되었습니다.");
    }
}