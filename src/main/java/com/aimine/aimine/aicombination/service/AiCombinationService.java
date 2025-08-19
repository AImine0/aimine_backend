package com.aimine.aimine.aicombination.service;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.aicombination.domain.AiCombination;
import com.aimine.aimine.aicombination.dto.AiCombinationDetailResponse;
import com.aimine.aimine.aicombination.dto.AiCombinationListResponse;
import com.aimine.aimine.aicombination.repository.AiCombinationRepository;
import com.aimine.aimine.aicombination.repository.AiCombinationServiceRepository;
import com.aimine.aimine.common.exception.BusinessException;
import com.aimine.aimine.common.exception.errorcode.AiServiceErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiCombinationService {

    private final AiCombinationRepository aiCombinationRepository;
    private final AiCombinationServiceRepository aiCombinationServiceRepository;

    /**
     * AI 조합 목록 조회
     */
    public AiCombinationListResponse getAiCombinations(String category, Boolean featured) {
        log.debug("AI 조합 목록 조회 요청: category={}, featured={}", category, featured);

        // 페이징 설정 (기본적으로 20개씩 조회)
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));

        Page<AiCombination> combinationsPage;

        if (category != null && !category.trim().isEmpty()) {
            // 카테고리별 조회
            combinationsPage = aiCombinationRepository.findByCategory(category, pageable);
        } else if (featured != null && featured) {
            // Featured 조합 조회 (임시로 전체 조회)
            combinationsPage = aiCombinationRepository.findFeaturedCombinations(pageable);
        } else {
            // 전체 조회
            combinationsPage = aiCombinationRepository.findAll(pageable);
        }

        List<AiCombination> combinations = combinationsPage.getContent();

        // 각 조합별 AI 서비스 목록 조회
        Map<Long, List<AiService>> combinationServicesMap = new HashMap<>();
        for (AiCombination combination : combinations) {
            List<AiService> services = aiCombinationServiceRepository.findAiServicesByCombination(combination);
            combinationServicesMap.put(combination.getId(), services);
        }

        return AiCombinationListResponse.from(combinations, combinationServicesMap);
    }

    /**
     * AI 조합 상세 조회
     */
    public AiCombinationDetailResponse getAiCombinationDetail(Long combinationId) {
        log.debug("AI 조합 상세 조회 요청: combinationId={}", combinationId);

        // AI 조합 조회
        AiCombination combination = aiCombinationRepository.findById(combinationId)
                .orElseThrow(() -> new BusinessException(AiServiceErrorCode.AI_COMBINATION_NOT_FOUND));

        // 조합에 포함된 AI 서비스 목록 조회
        List<AiService> aiServices = aiCombinationServiceRepository.findAiServicesByCombination(combination);

        return AiCombinationDetailResponse.from(combination, aiServices);
    }

    /**
     * 카테고리 목록 조회
     */
    public List<String> getCategories() {
        return aiCombinationRepository.findDistinctCategories();
    }

    /**
     * AI 조합 ID로 조회
     */
    public AiCombination findById(Long combinationId) {
        return aiCombinationRepository.findById(combinationId)
                .orElseThrow(() -> new BusinessException(AiServiceErrorCode.AI_COMBINATION_NOT_FOUND));
    }

    /**
     * 특정 AI 서비스가 포함된 조합 목록 조회
     */
    public List<AiCombination> getCombinationsByAiService(AiService aiService) {
        return aiCombinationServiceRepository.findCombinationsByAiService(aiService);
    }

    /**
     * 조합별 AI 서비스 개수 조회
     */
    public Long getAiServiceCount(AiCombination combination) {
        return aiCombinationServiceRepository.countAiServicesByCombination(combination);
    }
}