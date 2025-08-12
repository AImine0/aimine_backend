package com.aimine.features.jobs;

import com.aimine.core.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    @GetMapping
    public ResponseEntity<ApiResponse<String>> list() {
        return ResponseEntity.ok(ApiResponse.ok("jobs"));
    }
}