package com.aimine.features.categories;

import com.aimine.core.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService svc;

    public CategoryController(CategoryService s) {
        this.svc = s;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MainCategory>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(svc.findAllMain()));
    }
}