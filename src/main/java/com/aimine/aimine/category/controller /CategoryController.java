package com.aimine.aimine.category.controller;

import com.aimine.aimine.category.dto.CategoryListResponse;
import com.aimine.aimine.category.service.CategoryService;
import com.aimine.aimine.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "카테고리 관리", description = "카테고리 관련 API")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 목록 조회",
            description = "모든 카테고리 목록과 각 카테고리별 AI 서비스 개수를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<CategoryListResponse>> getAllCategories() {
        log.info("카테고리 목록 조회 요청");

        CategoryListResponse response = categoryService.getAllCategories();

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}