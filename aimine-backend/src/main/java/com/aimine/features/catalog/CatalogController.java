package com.aimine.features.catalog;

import com.aimine.core.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class CatalogController {
    private final CatalogService svc;

    public CatalogController(CatalogService s) {
        this.svc = s;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AiService>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(svc.list()));
    }
}