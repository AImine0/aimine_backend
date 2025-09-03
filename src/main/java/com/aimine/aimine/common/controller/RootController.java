package com.aimine.aimine.common.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
public class RootController {

    // GET /
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> root() {
        return Map.of(
                "ok", true,
                "service", "aimine-api",
                "time", OffsetDateTime.now().toString()
        );
    }

    // 선택: 프론트/모니터링용 헬스 엔드포인트
    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> health() {
        return Map.of("status", "UP");
    }
}
