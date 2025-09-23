package com.aimine.aimine.search.service;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.aiservice.repository.AiServiceRepository;
import com.aimine.aimine.keyword.domain.AiServiceKeyword;
import com.aimine.aimine.keyword.repository.AiServiceKeywordRepository;
import com.aimine.aimine.keyword.repository.KeywordRepository;
import com.aimine.aimine.search.dto.SearchResponse;
import com.aimine.aimine.search.dto.SearchSuggestion;
import com.aimine.aimine.search.dto.SearchSuggestionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
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
     * 확장된 통합 검색
     * - AI명, 카테고리, 기능키워드, 직업별 카테고리 통합 검색
     * - 별점 높은 순으로 정렬
     */
    public SearchResponse search(
            String query,
            String category,
            String pricing,
            String sort,
            int page,
            int size
    ) {
        log.info("확장된 검색 요청 - query: {}, category: {}, pricing: {}, sort: {}",
                query, category, pricing, sort);

        // 검색어가 없으면 기존 로직 사용
        if (!StringUtils.hasText(query)) {
            return performBasicSearch(null, category, pricing, sort, page, size);
        }

        // 확장된 검색 실행
        Set<AiService> searchResults = performEnhancedSearch(query, category, pricing);

        // Set을 List로 변환하고 별점 기준으로 정렬
        List<AiService> sortedResults = searchResults.stream()
                .sorted((a, b) -> {
                    // 별점 기준 내림차순 정렬
                    int ratingCompare = b.getAverageRating().compareTo(a.getAverageRating());
                    if (ratingCompare != 0) return ratingCompare;

                    // 별점이 같으면 서비스명 기준 오름차순
                    return a.getName().compareTo(b.getName());
                })
                .skip((long) page * size) // 페이지네이션
                .limit(size)
                .collect(Collectors.toList());

        // 결과 변환
        List<SearchResponse.AiServiceInfo> tools = sortedResults.stream()
                .map(this::convertToAiServiceInfo)
                .collect(Collectors.toList());

        // 추천 키워드 생성
        List<String> suggestedKeywords = generateSuggestedKeywords(query);

        return SearchResponse.builder()
                .query(query)
                .totalCount(searchResults.size())
                .tools(tools)
                .suggestedKeywords(suggestedKeywords)
                .build();
    }

    /**
     * 실시간 연관검색어 조회 (새로 추가)
     */
    public SearchSuggestionsResponse getSearchSuggestions(String query, int limit) {
        if (!StringUtils.hasText(query) || query.trim().length() < 1) {
            return SearchSuggestionsResponse.builder()
                    .suggestions(List.of())
                    .build();
        }

        String searchTerm = query.toLowerCase().trim();
        List<SearchSuggestion> suggestions = new ArrayList<>();

        // 우선순위 1: 기능별/직업별 카테고리 (AI 조합에서)
        List<SearchSuggestion> categorySuggestions = findCategorySuggestions(searchTerm);
        suggestions.addAll(categorySuggestions);

        // 우선순위 2: AI 서비스명
        List<SearchSuggestion> serviceSuggestions = findServiceSuggestions(searchTerm);
        suggestions.addAll(serviceSuggestions);

        // 우선순위 3: 키워드와 카테고리 displayName
        List<SearchSuggestion> keywordSuggestions = findKeywordSuggestions(searchTerm);
        suggestions.addAll(keywordSuggestions);

        // 중복 제거 및 제한
        return SearchSuggestionsResponse.builder()
                .suggestions(suggestions.stream()
                        .distinct()
                        .limit(limit)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 기능별/직업별 카테고리 연관검색어 조회 (우선순위 1)
     */
    private List<SearchSuggestion> findCategorySuggestions(String searchTerm) {
        List<SearchSuggestion> suggestions = new ArrayList<>();

        try {
            // AI 조합의 카테고리에서 검색어가 포함된 항목들 찾기
            List<AiService> categoryServices = aiServiceRepository.findByAiCombinationCategoryContainingIgnoreCase(searchTerm);

            // 카테고리별로 그룹화하여 중복 제거
            Set<String> categoryPaths = categoryServices.stream()
                    .map(service -> {
                        // AI 조합에서 카테고리 경로 생성 로직
                        // 실제로는 AiCombination 엔티티에서 category 정보를 가져와야 함
                        return "홈 > 직업별 > " + extractCategoryFromService(service);
                    })
                    .collect(Collectors.toSet());

            // 최대 3개까지만 반환
            categoryPaths.stream()
                    .limit(3)
                    .forEach(categoryPath -> {
                        suggestions.add(SearchSuggestion.builder()
                                .type(SearchSuggestion.SuggestionType.CATEGORY)
                                .text(extractCategoryName(categoryPath))
                                .categoryPath(categoryPath)
                                .build());
                    });

        } catch (Exception e) {
            log.warn("카테고리 연관검색어 조회 실패: {}", e.getMessage());
        }

        return suggestions;
    }

    /**
     * AI 서비스명 연관검색어 조회 (우선순위 2)
     */
    private List<SearchSuggestion> findServiceSuggestions(String searchTerm) {
        List<SearchSuggestion> suggestions = new ArrayList<>();

        try {
            // AI 서비스명에서 검색어가 포함된 서비스들 조회 (최대 5개)
            Page<AiService> services = aiServiceRepository.findByNameContainingIgnoreCase(
                    searchTerm, PageRequest.of(0, 5));

            services.getContent().forEach(service -> {
                suggestions.add(SearchSuggestion.builder()
                        .type(SearchSuggestion.SuggestionType.AI_SERVICE)
                        .id(service.getId())
                        .text(service.getName())
                        .logoUrl(buildImageUrl("https://aimine.up.railway.app", service.getImagePath()))
                        .tag(service.getCategory().getDisplayName())
                        .build());
            });

        } catch (Exception e) {
            log.warn("AI 서비스 연관검색어 조회 실패: {}", e.getMessage());
        }

        return suggestions;
    }

    /**
     * 키워드 및 카테고리 연관검색어 조회 (우선순위 3)
     */
    private List<SearchSuggestion> findKeywordSuggestions(String searchTerm) {
        List<SearchSuggestion> suggestions = new ArrayList<>();

        try {
            // 1. 기능 키워드에서 검색
            List<String> keywordNames = keywordRepository.findByNameContainingIgnoreCase(searchTerm)
                    .stream()
                    .map(keyword -> keyword.getName())
                    .limit(3)
                    .collect(Collectors.toList());

            keywordNames.forEach(keywordName -> {
                suggestions.add(SearchSuggestion.builder()
                        .type(SearchSuggestion.SuggestionType.KEYWORD)
                        .text(keywordName)
                        .build());
            });

            // 2. AI 카테고리 displayName에서 검색
            List<AiService> categoryServices = aiServiceRepository.findByCategoryDisplayNameContainingIgnoreCase(searchTerm);

            // 카테고리별로 그룹화하여 중복 제거
            Set<String> categoryNames = categoryServices.stream()
                    .map(service -> service.getCategory().getDisplayName())
                    .collect(Collectors.toSet());

            // 최대 2개까지만 추가
            categoryNames.stream()
                    .limit(2)
                    .forEach(categoryName -> {
                        suggestions.add(SearchSuggestion.builder()
                                .type(SearchSuggestion.SuggestionType.KEYWORD)
                                .text(categoryName)
                                .build());
                    });

        } catch (Exception e) {
            log.warn("키워드 연관검색어 조회 실패: {}", e.getMessage());
        }

        return suggestions;
    }

    /**
     * 서비스에서 카테고리명 추출 (헬퍼 메서드)
     */
    private String extractCategoryFromService(AiService service) {
        // AI 조합 카테고리 정보가 필요하지만, 현재는 서비스의 카테고리 사용
        return service.getCategory().getDisplayName();
    }

    /**
     * 카테고리 경로에서 카테고리명만 추출 (헬퍼 메서드)
     */
    private String extractCategoryName(String categoryPath) {
        // "홈 > 직업별 > 디자인" → "디자인"
        if (categoryPath.contains(" > ")) {
            String[] parts = categoryPath.split(" > ");
            return parts[parts.length - 1];
        }
        return categoryPath;
    }

    /**
     * 확장된 검색 실행 - 여러 조건으로 검색하여 중복 제거
     */
    private Set<AiService> performEnhancedSearch(String query, String category, String pricing) {
        Set<AiService> allResults = new HashSet<>();

        // 가격 타입 변환
        AiService.PricingType pricingType = null;
        if (StringUtils.hasText(pricing)) {
            try {
                pricingType = AiService.PricingType.valueOf(pricing.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 가격 타입: {}", pricing);
            }
        }

        String searchTerm = query.toLowerCase().trim();

        // 1. AI 서비스명으로 검색
        List<AiService> nameResults = searchByServiceName(searchTerm, category, pricingType);
        allResults.addAll(nameResults);
        log.info("서비스명 검색 결과: {}개", nameResults.size());

        // 2. AI 카테고리로 검색 (display_name)
        List<AiService> categoryResults = searchByCategory(searchTerm, pricingType);
        allResults.addAll(categoryResults);
        log.info("카테고리 검색 결과: {}개", categoryResults.size());

        // 3. 기능 키워드로 검색
        List<AiService> keywordResults = searchByKeywords(searchTerm, category, pricingType);
        allResults.addAll(keywordResults);
        log.info("키워드 검색 결과: {}개", keywordResults.size());

        // 4. 직업별 카테고리로 검색 (AI조합에서 카테고리 검색)
        List<AiService> jobCategoryResults = searchByJobCategory(searchTerm, pricingType);
        allResults.addAll(jobCategoryResults);
        log.info("직업별 카테고리 검색 결과: {}개", jobCategoryResults.size());

        log.info("전체 통합 검색 결과 (중복 제거 후): {}개", allResults.size());
        return allResults;
    }

    /**
     * 기존 검색 로직 (호환성 유지)
     */
    private SearchResponse performBasicSearch(String query, String category, String pricing, String sort, int page, int size) {
        Sort sortCondition = createSortCondition(sort);
        Pageable pageable = PageRequest.of(page, size, sortCondition);

        Page<AiService> searchResults = performSearch(query, category, pricing, pageable);

        List<SearchResponse.AiServiceInfo> tools = searchResults.getContent().stream()
                .map(this::convertToAiServiceInfo)
                .collect(Collectors.toList());

        List<String> suggestedKeywords = generateSuggestedKeywords(query);

        return SearchResponse.builder()
                .query(query)
                .totalCount((int) searchResults.getTotalElements())
                .tools(tools)
                .suggestedKeywords(suggestedKeywords)
                .build();
    }

    /**
     * 기존 검색 메서드 (내부적으로 사용)
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
     * 1. AI 서비스명으로 검색
     */
    private List<AiService> searchByServiceName(String searchTerm, String category, AiService.PricingType pricingType) {
        Page<AiService> page;

        if (StringUtils.hasText(category) && pricingType != null) {
            page = aiServiceRepository.findByNameContainingIgnoreCaseAndCategoryDisplayNameAndPricingType(
                    searchTerm, category, pricingType, Pageable.unpaged());
        } else if (StringUtils.hasText(category)) {
            page = aiServiceRepository.findByNameContainingIgnoreCaseAndCategoryDisplayName(
                    searchTerm, category, Pageable.unpaged());
        } else if (pricingType != null) {
            page = aiServiceRepository.findByNameContainingIgnoreCaseAndPricingType(
                    searchTerm, pricingType, Pageable.unpaged());
        } else {
            page = aiServiceRepository.findByNameContainingIgnoreCase(searchTerm, Pageable.unpaged());
        }

        return page.getContent();
    }

    /**
     * 2. AI 카테고리로 검색
     */
    private List<AiService> searchByCategory(String searchTerm, AiService.PricingType pricingType) {
        if (pricingType != null) {
            return aiServiceRepository.findByCategoryDisplayNameContainingIgnoreCaseAndPricingType(
                    searchTerm, pricingType);
        } else {
            return aiServiceRepository.findByCategoryDisplayNameContainingIgnoreCase(searchTerm);
        }
    }

    /**
     * 3. 기능 키워드로 검색
     */
    private List<AiService> searchByKeywords(String searchTerm, String category, AiService.PricingType pricingType) {
        // 키워드명에 검색어가 포함된 키워드들 찾기
        List<Long> keywordIds = keywordRepository.findByNameContainingIgnoreCase(searchTerm)
                .stream()
                .map(keyword -> keyword.getId())
                .collect(Collectors.toList());

        if (keywordIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 해당 키워드들과 연결된 AI 서비스들 찾기 (AiServiceRepository에 추가한 메서드 사용)
        List<AiService> keywordServices = aiServiceRepository.findAiServicesByKeywordIds(keywordIds);

        // 카테고리/가격 필터 적용
        return keywordServices.stream()
                .filter(service -> {
                    if (StringUtils.hasText(category) && !category.equals(service.getCategory().getDisplayName())) {
                        return false;
                    }
                    if (pricingType != null && !pricingType.equals(service.getPricingType())) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * 4. 직업별 카테고리로 검색 (AI 조합의 카테고리)
     */
    private List<AiService> searchByJobCategory(String searchTerm, AiService.PricingType pricingType) {
        List<AiService> jobCategoryServices = aiServiceRepository.findByAiCombinationCategoryContainingIgnoreCase(searchTerm);

        // 가격 필터 적용
        if (pricingType != null) {
            return jobCategoryServices.stream()
                    .filter(service -> pricingType.equals(service.getPricingType()))
                    .collect(Collectors.toList());
        }

        return jobCategoryServices;
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
            case "popular":
                return Sort.by(Sort.Direction.DESC, "averageRating");
            case "latest":
            case "newest":
                return Sort.by(Sort.Direction.DESC, "releaseDate");
            case "name":
                return Sort.by(Sort.Direction.ASC, "name");
            default:
                return Sort.by(Sort.Direction.DESC, "averageRating");
        }
    }

    /**
     * 이미지 URL을 안전하게 생성하는 헬퍼 메서드
     */
    private String buildImageUrl(String baseUrl, String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return baseUrl + "/images/Logo/Logo_FINAL.svg";
        }
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath;
        }
        return baseUrl + (imagePath.startsWith("/") ? imagePath : "/" + imagePath);
    }

    /**
     * AiService를 AiServiceInfo로 변환
     */
    private SearchResponse.AiServiceInfo convertToAiServiceInfo(AiService aiService) {
        String baseUrl = "https://aimine.up.railway.app";

        // 해당 서비스의 키워드 조회 (최대 5개)
        List<String> keywords = aiServiceKeywordRepository.findKeywordsByAiService(aiService)
                .stream()
                .map(keyword -> keyword.getName())
                .limit(5)
                .collect(Collectors.toList());

        return SearchResponse.AiServiceInfo.builder()
                .id(aiService.getId())
                .serviceName(aiService.getName())
                .description(aiService.getDescription() != null ?
                        aiService.getDescription() : aiService.getName() + " AI 서비스")
                .logoUrl(buildImageUrl(baseUrl, aiService.getImagePath()))
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
        List<String> defaultKeywords = Arrays.asList(
                "챗봇", "ChatGPT", "이미지 생성", "콘텐츠 작성",
                "업무 자동화", "교육/연구", "기획/마케팅", "AI 코드 어시스턴트"
        );

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