package com.aimine.features.search;

import com.aimine.core.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    @GetMapping
    public ResponseEntity<ApiResponse<String>> search(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.ok("q=" + q));
    }
}