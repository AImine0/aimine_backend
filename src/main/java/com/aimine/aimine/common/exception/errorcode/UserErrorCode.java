package com.aimine.aimine.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {

    // 404 Not Found
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 400 Bad Request
    INVALID_USER_INFO("U002", "잘못된 사용자 정보입니다.", HttpStatus.BAD_REQUEST),

    // 409 Conflict
    USER_ALREADY_EXISTS("U003", "이미 존재하는 사용자입니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}