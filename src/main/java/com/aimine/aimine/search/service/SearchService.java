package com.aimine.aimine.search.service;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.aiservice.repository.AiServiceRepository;
import com.aimine.aimine.keyword.domain.AiServiceKeyword;
import com.aimine.aimine.keyword.repository.AiServiceKeywordRepository;
import com.aimine.aimine.keyword.repository.KeywordRepository;
import com.aimine.aimine.search.dto.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final AiServiceRepository aiServiceRepository;
    private final KeywordRepository keywordRepository;
    private final AiServiceKeywordRepository aiServiceKeywordRepository;

    /**
     * 통합 검색
     */
    public SearchResponse search(
            String query,
            String category,
            String pricing,
            String sort,
            int page,
            int size
    ) {
        log.info("검색 요청 - query: {}, category: {}, pricing: {}, sort: {}",
                query, category, pricing, sort);

        // 정렬 조건 설정
        Sort sortCondition = createSortCondition(sort);
        Pageable pageable = PageRequest.of(page, size, sortCondition);

        // 검색 실행
        Page<AiService> searchResults = performSearch(query, category, pricing, pageable);

        // 결과 변환
        List<SearchResponse.AiServiceInfo> tools = searchResults.getContent().stream()
                .map(this::convertToAiServiceInfo)
                .collect(Collectors.toList());

        // 추천 키워드 생성
        List<String> suggestedKeywords = generateSuggestedKeywords(query);

        return SearchResponse.builder()
                .query(query)
                .totalCount((int) searchResults.getTotalElements())
                .tools(tools)
                .suggestedKeywords(suggestedKeywords)
                .build();
    }

    /**
     * 실제 검색 수행
     */
    private Page<AiService> performSearch(String query, String category, String pricing, Pageable pageable) {
        // 모든 조건이 없으면 전체 조회
        if (!StringUtils.hasText(query) && !StringUtils.hasText(category) && !StringUtils.hasText(pricing)) {
            return aiServiceRepository.findAll(pageable);
        }

        // 검색어만 있는 경우
        if (StringUtils.hasText(query) && !StringUtils.hasText(category) && !StringUtils.hasText(pricing)) {
            return aiServiceRepository.findByNameContainingIgnoreCase(query, pageable);
        }

        // 카테고리 + 가격 필터링 (검색어 없음)
        if (!StringUtils.hasText(query) && StringUtils.hasText(category) && StringUtils.hasText(pricing)) {
            return aiServiceRepository.findByCategoryDisplayNameAndPricingType(
                    category,
                    AiService.PricingType.valueOf(pricing.toUpperCase()),
                    pageable
            );
        }

        // 검색어 + 카테고리
        if (StringUtils.hasText(query) && StringUtils.hasText(category) && !StringUtils.hasText(pricing)) {
            return aiServiceRepository.findByNameContainingIgnoreCaseAndCategoryDisplayName(
                    query, category, pageable
            );
        }

        // 검색어 + 가격
        if (StringUtils.hasText(query) && !StringUtils.hasText(category) && StringUtils.hasText(pricing)) {
            return aiServiceRepository.findByNameContainingIgnoreCaseAndPricingType(
                    query,
                    AiService.PricingType.valueOf(pricing.toUpperCase()),
                    pageable
            );
        }

        // 모든 조건이 있는 경우
        if (StringUtils.hasText(query) && StringUtils.hasText(category) && StringUtils.hasText(pricing)) {
            return aiServiceRepository.findByNameContainingIgnoreCaseAndCategoryDisplayNameAndPricingType(
                    query,
                    category,
                    AiService.PricingType.valueOf(pricing.toUpperCase()),
                    pageable
            );
        }

        // 카테고리만 있는 경우
        if (StringUtils.hasText(category)) {
            return aiServiceRepository.findByCategoryDisplayName(category, pageable);
        }

        // 가격만 있는 경우
        if (StringUtils.hasText(pricing)) {
            return aiServiceRepository.findByPricingType(
                    AiService.PricingType.valueOf(pricing.toUpperCase()),
                    pageable
            );
        }

        return aiServiceRepository.findAll(pageable);
    }

    /**
     * 정렬 조건 생성
     */
    private Sort createSortCondition(String sort) {
        if (!StringUtils.hasText(sort)) {
            return Sort.by(Sort.Direction.DESC, "averageRating");
        }

        switch (sort.toLowerCase()) {
            case "rating":
                return Sort.by(Sort.Direction.DESC, "averageRating");
            case "latest":
                return Sort.by(Sort.Direction.DESC, "releaseDate");
            case "name":
                return Sort.by(Sort.Direction.ASC, "name");
            case "recommendation":
                return Sort.by(Sort.Direction.ASC, "recommendationRank");
            default:
                return Sort.by(Sort.Direction.DESC, "averageRating");
        }
    }

    /**
     * AiService를 AiServiceInfo로 변환
     */
    /**
     * AiService를 AiServiceInfo로 변환
     */
    /**
     * AiService를 AiServiceInfo로 변환
     */
    private SearchResponse.AiServiceInfo convertToAiServiceInfo(AiService aiService) {
        // 임시로 빈 키워드 리스트 사용 (나중에 실제 데이터로 대체)
        List<String> keywords = List.of("텍스트 생성", "대화", "AI 어시스턴트");

        return SearchResponse.AiServiceInfo.builder()
                .id(aiService.getId())
                .serviceName(aiService.getName())
                .description("AI 서비스 설명")
                .logoUrl("https://example.com/logo.png")
                .categoryName(aiService.getCategory().getDisplayName())
                .pricingType(aiService.getPricingType().name().toLowerCase())
                .overallRating(aiService.getAverageRating())
                .keywords(keywords)
                .build();
    }

    /**
     * 추천 키워드 생성
     */
    private List<String> generateSuggestedKeywords(String query) {
        // 기본 추천 키워드
        List<String> defaultKeywords = Arrays.asList(
                "챗봇", "ChatGPT", "이미지 생성", "콘텐츠 작성",
                "업무 자동화", "교육/연구", "기획/마케팅", "AI 코드 어시스턴트"
        );

        // 실제로는 검색어와 관련된 키워드를 DB에서 조회
        if (StringUtils.hasText(query)) {
            List<String> relatedKeywords = keywordRepository.findByNameContainingIgnoreCase(query)
                    .stream()
                    .map(keyword -> keyword.getName())
                    .limit(5)
                    .collect(Collectors.toList());

            if (!relatedKeywords.isEmpty()) {
                return relatedKeywords;
            }
        }

        return defaultKeywords;
    }
}