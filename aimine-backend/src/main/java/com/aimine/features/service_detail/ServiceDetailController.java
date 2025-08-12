package com.aimine.features.service_detail;

import com.aimine.core.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/services")
public class ServiceDetailController {
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> detail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("detail " + id));
    }
}