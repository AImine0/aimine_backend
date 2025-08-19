package com.aimine.aimine.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BookmarkErrorCode implements ErrorCode {

    // 404 Not Found
    BOOKMARK_NOT_FOUND("B001", "북마크를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 409 Conflict
    BOOKMARK_ALREADY_EXISTS("B002", "이미 북마크된 서비스입니다.", HttpStatus.CONFLICT),

    // 400 Bad Request
    INVALID_BOOKMARK_REQUEST("B003", "잘못된 북마크 요청입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}