package com.aimine.features.reviews;

import com.aimine.core.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @GetMapping
    public ResponseEntity<ApiResponse<String>> list() {
        return ResponseEntity.ok(ApiResponse.ok("reviews"));
    }
}