package com.aimine.aimine.common.exception;

import com.aimine.aimine.common.exception.errorcode.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * BusinessException 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("BusinessException occurred: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage()
        );

        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(errorResponse);
    }

    /**
     * Validation 관련 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Validation error occurred: {}", e.getMessage());

        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse(CommonErrorCode.INVALID_PARAMETER.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                CommonErrorCode.INVALID_PARAMETER.getCode(),
                message
        );

        return ResponseEntity
                .status(CommonErrorCode.INVALID_PARAMETER.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * @RequestParam 관련 예외 처리
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("Missing parameter error occurred: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                CommonErrorCode.MISSING_PARAMETER.getCode(),
                "필수 파라미터가 누락되었습니다: " + e.getParameterName()
        );

        return ResponseEntity
                .status(CommonErrorCode.MISSING_PARAMETER.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * HTTP Method 지원하지 않음
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("Method not supported error occurred: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                CommonErrorCode.INVALID_REQUEST.getCode(),
                "지원하지 않는 HTTP 메소드입니다: " + e.getMethod()
        );

        return ResponseEntity
                .status(CommonErrorCode.INVALID_REQUEST.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * 타입 변환 실패
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("Type mismatch error occurred: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                CommonErrorCode.INVALID_PARAMETER.getCode(),
                "잘못된 파라미터 타입입니다: " + e.getName()
        );

        return ResponseEntity
                .status(CommonErrorCode.INVALID_PARAMETER.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * JSON 파싱 오류
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("JSON parsing error occurred: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                CommonErrorCode.INVALID_REQUEST.getCode(),
                "JSON 형식이 올바르지 않습니다."
        );

        return ResponseEntity
                .status(CommonErrorCode.INVALID_REQUEST.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * 404 Not Found
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("No handler found error occurred: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                CommonErrorCode.RESOURCE_NOT_FOUND.getCode(),
                "요청한 리소스를 찾을 수 없습니다."
        );

        return ResponseEntity
                .status(CommonErrorCode.RESOURCE_NOT_FOUND.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * 기타 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected error occurred", e);

        ErrorResponse errorResponse = ErrorResponse.of(
                CommonErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                CommonErrorCode.INTERNAL_SERVER_ERROR.getMessage()
        );

        return ResponseEntity
                .status(CommonErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(errorResponse);
    }
}