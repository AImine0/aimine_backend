package com.aimine.aimine.auth.controller;

import com.aimine.aimine.auth.dto.AuthGoogleLoginResponse;
import com.aimine.aimine.auth.dto.AuthLogoutResponse;
import com.aimine.aimine.auth.dto.UserProfileResponse;
import com.aimine.aimine.auth.service.AuthService;
import com.aimine.aimine.common.dto.ApiResponse;
import com.aimine.aimine.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관리 API")
public class AuthController {

    private final AuthService authService;

    /**
     * 구글 로그인
     */
    @PostMapping("/google-login")
    @Operation(summary = "구글 로그인", description = "구글 토큰으로 로그인/회원가입을 처리합니다.")
    public ResponseEntity<AuthGoogleLoginResponse> googleLogin(
            @Parameter(description = "구글 액세스 토큰", required = true)
            @RequestBody String googleToken
    ) {
        log.info("구글 로그인 요청");

        AuthGoogleLoginResponse response = authService.processGoogleLogin(googleToken);

        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "현재 사용자를 로그아웃합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<AuthLogoutResponse> logout(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authorization
    ) {
        log.info("로그아웃 요청");

        // Bearer 토큰에서 실제 토큰 추출
        String accessToken = authorization.substring(7);

        AuthLogoutResponse response = authService.logout(accessToken);

        return ResponseEntity.ok(response);
    }

    /**
     * 현재 사용자 정보 조회
     */
    @GetMapping("/me")
    @Operation(
            summary = "사용자 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserProfileResponse> getCurrentUser(
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user
    ) {
        log.info("사용자 정보 조회 요청: {}", user.getEmail());

        UserProfileResponse response = authService.getCurrentUser(user);

        return ResponseEntity.ok(response);
    }
}