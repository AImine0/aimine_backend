package com.aimine.features.bookmarks;

import com.aimine.core.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    @GetMapping
    public ResponseEntity<ApiResponse<String>> list() {
        return ResponseEntity.ok(ApiResponse.ok("bookmarks"));
    }
}