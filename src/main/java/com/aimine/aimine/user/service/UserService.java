package com.aimine.aimine.user.service;

import com.aimine.aimine.common.exception.BusinessException;
import com.aimine.aimine.common.exception.errorcode.UserErrorCode;
import com.aimine.aimine.user.domain.User;
import com.aimine.aimine.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * 사용자 ID로 조회
     */
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    /**
     * Google ID로 사용자 조회
     */
    public User findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    /**
     * 이메일로 사용자 조회
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    /**
     * Google ID로 사용자 존재 여부 확인
     */
    public boolean existsByGoogleId(String googleId) {
        return userRepository.existsByGoogleId(googleId);
    }

    /**
     * 새 사용자 생성 (OAuth 회원가입)
     */
    @Transactional
    public User createUser(String googleId, String email, String name) {
        // 중복 검사
        if (userRepository.existsByGoogleId(googleId)) {
            throw new BusinessException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .googleId(googleId)
                .email(email)
                .name(name)
                .role(User.Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("새 사용자 생성: {}", savedUser.getEmail());

        return savedUser;
    }

    /**
     * 사용자 프로필 업데이트
     */
    @Transactional
    public User updateProfile(Long userId, String name) {
        User user = findById(userId);
        user.updateProfile(name);

        log.info("사용자 프로필 업데이트: {}", user.getEmail());
        return user;
    }

    /**
     * Google 로그인/회원가입 처리
     */
    @Transactional
    public User processGoogleLogin(String googleId, String email, String name) {
        return userRepository.findByGoogleId(googleId)
                .orElseGet(() -> {
                    log.info("신규 Google 사용자 회원가입: {}", email);
                    return createUser(googleId, email, name);
                });
    }
}