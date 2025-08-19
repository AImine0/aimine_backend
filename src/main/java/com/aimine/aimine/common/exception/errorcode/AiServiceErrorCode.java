package com.aimine.aimine.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AiServiceErrorCode implements ErrorCode {

    // 404 Not Found
    AI_SERVICE_NOT_FOUND("S001", "AI 서비스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND("S002", "카테고리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    KEYWORD_NOT_FOUND("S003", "키워드를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    AI_COMBINATION_NOT_FOUND("S004", "AI 조합을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 400 Bad Request
    INVALID_SEARCH_QUERY("S005", "잘못된 검색 쿼리입니다.", HttpStatus.BAD_REQUEST),
    INVALID_FILTER_PARAMETER("S006", "잘못된 필터 파라미터입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}