package com.aimine.aimine.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private Map<String, Object> body(String code, String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("code", code);
        map.put("message", msg);
        map.put("timestamp", OffsetDateTime.now().toString());
        return map;
    }

    /** 401: 인증 실패 */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        log.warn("[{} {}] 401 Unauthorized: {}", req.getMethod(), req.getRequestURI(), ex.toString(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(body("A401", "인증이 필요합니다."));
    }

    /** 403: 권한 없음 */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccess(AccessDeniedException ex, HttpServletRequest req) {
        log.warn("[{} {}] 403 Forbidden: {}", req.getMethod(), req.getRequestURI(), ex.toString(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(body("A403", "접근 권한이 없습니다."));
    }

    /** 404: 핸들러 없음 */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNotFound(NoHandlerFoundException ex, HttpServletRequest req) {
        log.info("[{} {}] 404 Not Found: {}", req.getMethod(), req.getRequestURI(), ex.toString(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body("R404", "요청하신 경로를 찾을 수 없습니다."));
    }

    /** 400: 검증/형식 오류 */
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<?> handleBadRequest(Exception ex, HttpServletRequest req) {
        log.info("[{} {}] 400 Bad Request: {}", req.getMethod(), req.getRequestURI(), ex.toString(), ex);
        return ResponseEntity.badRequest()
                .body(body("V400", "요청 형식이 올바르지 않습니다."));
    }

    /** 409: 무결성 위반 */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("[{} {}] 409 Conflict: {}", req.getMethod(), req.getRequestURI(), ex.toString(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(body("D409", "데이터 무결성에 위배됩니다."));
    }

    /** 500: 그 외 모든 예외(스택 포함 로깅) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex, HttpServletRequest req) {
        log.error("[{} {}] 500 Internal Server Error: {}", req.getMethod(), req.getRequestURI(), ex.toString(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body("C500", "서버 내부 오류가 발생했습니다."));
    }
}
