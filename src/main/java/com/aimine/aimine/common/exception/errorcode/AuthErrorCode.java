package com.aimine.aimine.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    // 401 Unauthorized
    UNAUTHORIZED("A001", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("A002", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("A003", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),

    // 403 Forbidden
    FORBIDDEN("A004", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // OAuth 관련
    OAUTH_LOGIN_FAILED("A005", "OAuth 로그인에 실패했습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}