package com.aimine.aimine.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    // 400 Bad Request
    INVALID_REQUEST("C001", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    INVALID_PARAMETER("C002", "잘못된 파라미터입니다.", HttpStatus.BAD_REQUEST),
    MISSING_PARAMETER("C003", "필수 파라미터가 누락되었습니다.", HttpStatus.BAD_REQUEST),

    // 404 Not Found
    RESOURCE_NOT_FOUND("C004", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR("C500", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}