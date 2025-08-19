package com.aimine.aimine.aiservice.service;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.aiservice.dto.ServiceDetailResponse;
import com.aimine.aimine.aiservice.dto.ServiceListResponse;
import com.aimine.aimine.aiservice.repository.AiServiceRepository;
import com.aimine.aimine.category.domain.Category;
import com.aimine.aimine.category.repository.CategoryRepository;
import com.aimine.aimine.common.exception.BusinessException;
import com.aimine.aimine.common.exception.errorcode.AiServiceErrorCode;
import com.aimine.aimine.common.util.Direction;
import com.aimine.aimine.common.util.SortedBy;
import com.aimine.aimine.keyword.domain.Keyword;
import com.aimine.aimine.keyword.repository.AiServiceKeywordRepository;
import com.aimine.aimine.review.domain.Review;
import com.aimine.aimine.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiServiceService {

    private final AiServiceRepository aiServiceRepository;
    private final CategoryRepository categoryRepository;
    private final AiServiceKeywordRepository aiServiceKeywordRepository;
    private final ReviewRepository reviewRepository;

    /**
     * AI 서비스 목록 조회 (페이징, 필터링, 정렬 지원)
     */
    public ServiceListResponse getAiServices(
            int page,
            int size,
            String categoryName,
            String search,
            String sort,
            String pricing
    ) {
        log.debug("AI 서비스 목록 조회: page={}, size={}, category={}, search={}, sort={}, pricing={}",
                page, size, categoryName, search, sort, pricing);

        // 페이징 및 정렬 처리
        Pageable pageable = createPageable(page, size, sort);

        // 카테고리 필터링
        Category category = null;
        if (categoryName != null && !categoryName.trim().isEmpty()) {
            category = categoryRepository.findByName(categoryName).orElse(null);
        }

        // 가격 타입 필터링
        AiService.PricingType pricingType = null;
        if (pricing != null && !pricing.trim().isEmpty()) {
            try {
                pricingType = AiService.PricingType.valueOf(pricing.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(AiServiceErrorCode.INVALID_FILTER_PARAMETER,
                        "유효하지 않은 가격 타입: " + pricing);
            }
        }

        // 조건에 따른 조회
        Page<AiService> aiServicesPage;
        if (search != null && !search.trim().isEmpty()) {
            // 검색어가 있는 경우
            aiServicesPage = aiServiceRepository.findByNameContaining(search, pageable);
        } else if (category != null || pricingType != null) {
            // 필터링이 있는 경우
            aiServicesPage = aiServiceRepository.findBySearchCriteria(search, category, pricingType, pageable);
        } else {
            // 기본 조회
            aiServicesPage = aiServiceRepository.findAll(pageable);
        }

        List<AiService> aiServices = aiServicesPage.getContent();

        // 각 AI 서비스별 키워드 조회
        List<List<String>> keywordsList = aiServices.stream()
                .map(service -> {
                    List<Keyword> keywords = aiServiceKeywordRepository.findKeywordsByAiService(service);
                    return keywords.stream()
                            .map(Keyword::getName)
                            .collect(Collectors.toList());
                })
                .collect(Collectors.toList());

        return ServiceListResponse.from(aiServices, keywordsList);
    }

    /**
     * AI 서비스 상세 조회
     */
    public ServiceDetailResponse getAiServiceDetail(Long serviceId) {
        log.debug("AI 서비스 상세 조회: serviceId={}", serviceId);

        // AI 서비스 조회
        AiService aiService = aiServiceRepository.findById(serviceId)
                .orElseThrow(() -> new BusinessException(AiServiceErrorCode.AI_SERVICE_NOT_FOUND));

        // 키워드 조회
        List<Keyword> keywords = aiServiceKeywordRepository.findKeywordsByAiService(aiService);

        // 리뷰 조회 (최신 순으로 제한)
        PageRequest reviewPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewsPage = reviewRepository.findByAiService(aiService, reviewPageable);
        List<Review> reviews = reviewsPage.getContent();

        return ServiceDetailResponse.from(aiService, keywords, reviews);
    }

    /**
     * 페이징 및 정렬 조건 생성
     */
    private Pageable createPageable(int page, int size, String sortParam) {
        // 기본값 설정
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;

        // 정렬 처리
        Sort sort = createSort(sortParam);

        return PageRequest.of(page, size, sort);
    }

    /**
     * 정렬 조건 생성
     */
    private Sort createSort(String sortParam) {
        if (sortParam == null || sortParam.trim().isEmpty()) {
            // 기본 정렬: 평점 내림차순
            return Sort.by(Sort.Direction.DESC, "averageRating");
        }

        try {
            SortedBy sortedBy = SortedBy.fromString(sortParam);

            switch (sortedBy) {
                case RATING:
                    return Sort.by(Sort.Direction.DESC, "averageRating");
                case LATEST:
                    return Sort.by(Sort.Direction.DESC, "releaseDate");
                case NAME:
                    return Sort.by(Sort.Direction.ASC, "name");
                case RECOMMENDATION:
                    return Sort.by(Sort.Direction.ASC, "recommendationRank");
                default:
                    return Sort.by(Sort.Direction.DESC, "averageRating");
            }
        } catch (Exception e) {
            log.warn("Invalid sort parameter: {}, using default sort", sortParam);
            return Sort.by(Sort.Direction.DESC, "averageRating");
        }
    }

    /**
     * AI 서비스 ID로 조회
     */
    public AiService findById(Long serviceId) {
        return aiServiceRepository.findById(serviceId)
                .orElseThrow(() -> new BusinessException(AiServiceErrorCode.AI_SERVICE_NOT_FOUND));
    }
}