package com.aimine.aimine.category.service;

import com.aimine.aimine.aiservice.repository.AiServiceRepository;
import com.aimine.aimine.category.domain.Category;
import com.aimine.aimine.category.dto.CategoryListResponse;
import com.aimine.aimine.category.repository.CategoryRepository;
import com.aimine.aimine.common.exception.BusinessException;
import com.aimine.aimine.common.exception.errorcode.AiServiceErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AiServiceRepository aiServiceRepository;

    /**
     * 카테고리 목록 조회
     */
    public CategoryListResponse getAllCategories() {
        log.debug("카테고리 목록 조회 요청");

        List<Category> categories = categoryRepository.findAll();

        // 각 카테고리별 AI 서비스 개수 조회
        List<Long> serviceCounts = categories.stream()
                .map(category -> aiServiceRepository.countByCategory(category))
                .collect(Collectors.toList());

        return CategoryListResponse.from(categories, serviceCounts);
    }

    /**
     * 카테고리 이름으로 조회
     */
    public Category findByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new BusinessException(AiServiceErrorCode.CATEGORY_NOT_FOUND));
    }

    /**
     * 카테고리 ID로 조회
     */
    public Category findById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(AiServiceErrorCode.CATEGORY_NOT_FOUND));
    }

    /**
     * 카테고리 존재 확인
     */
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
}