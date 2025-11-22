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

    public AiCombinationListResponse getAiCombinations(String category, Boolean featured) {
        log.debug("AI 조합 목록 조회 요청: category={}, featured={}", category, featured);
    
        List<AiCombination> combinations;
    
        if (category != null && !category.trim().isEmpty()) {
            combinations = aiCombinationRepository.findByCategory(category, 
                org.springframework.data.domain.PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "id"))).getContent();
        } else {
            combinations = aiCombinationRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        }
    
        Map<Long, List<AiService>> combinationServicesMap = new HashMap<>();
        for (AiCombination combination : combinations) {
            List<AiService> services = aiCombinationServiceRepository.findAiServicesByCombination(combination);
            combinationServicesMap.put(combination.getId(), services);
        }
    
        return AiCombinationListResponse.from(combinations, combinationServicesMap);
    }

    public AiCombinationDetailResponse getAiCombinationDetail(Long combinationId) {
        log.debug("AI 조합 상세 조회 요청: combinationId={}", combinationId);

        AiCombination combination = aiCombinationRepository.findById(combinationId)
                .orElseThrow(() -> new BusinessException(AiServiceErrorCode.AI_COMBINATION_NOT_FOUND));

        List<AiService> aiServices = aiCombinationServiceRepository.findAiServicesByCombination(combination);

        return AiCombinationDetailResponse.from(combination, aiServices);
    }

    public List<String> getCategories() {
        return aiCombinationRepository.findDistinctCategories();
    }

    public AiCombination findById(Long combinationId) {
        return aiCombinationRepository.findById(combinationId)
                .orElseThrow(() -> new BusinessException(AiServiceErrorCode.AI_COMBINATION_NOT_FOUND));
    }

    public List<AiCombination> getCombinationsByAiService(AiService aiService) {
        return aiCombinationServiceRepository.findCombinationsByAiService(aiService);
    }

    public Long getAiServiceCount(AiCombination combination) {
        return aiCombinationServiceRepository.countAiServicesByCombination(combination);
    }
}
