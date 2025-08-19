package com.aimine.aimine.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

    // 404 Not Found
    REVIEW_NOT_FOUND("R001", "리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 409 Conflict
    REVIEW_ALREADY_EXISTS("R002", "이미 리뷰를 작성한 서비스입니다.", HttpStatus.CONFLICT),

    // 400 Bad Request
    INVALID_RATING("R003", "잘못된 평점입니다. (1-5 사이의 값을 입력해주세요)", HttpStatus.BAD_REQUEST),
    INVALID_REVIEW_REQUEST("R004", "잘못된 리뷰 요청입니다.", HttpStatus.BAD_REQUEST),

    // 403 Forbidden
    REVIEW_ACCESS_DENIED("R005", "리뷰 수정/삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}