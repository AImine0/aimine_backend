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
import java.util.Map;
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
     * AI 서비스 목록 조회 (페이징, 필터링, 정렬) - N+1 문제 해결
     */
    public ServiceListResponse getAiServices(
            int page,
            int size,
            String category,
            String search,
            String sort,
            String pricing) {

        log.debug("AI 서비스 목록 조회: page={}, size={}, category={}, search={}, sort={}, pricing={}",
                page, size, category, search, sort, pricing);

        // 페이지 크기 제한 (성능상 최대 50개)
        size = Math.min(size, 50);

        // 정렬 조건 생성
        Pageable pageable = createPageable(page, size, sort);

        // 🔥 N+1 해결: 카테고리 정보를 포함하여 조회
        Page<AiService> aiServicesPage;

        // 카테고리 조회가 있는 경우
        if (category != null && !category.trim().isEmpty()) {
            Category categoryEntity = categoryRepository.findByName(category)
                    .orElse(null);

            if (categoryEntity != null) {
                // 카테고리별 조회 시 카테고리 정보 포함
                aiServicesPage = aiServiceRepository.findByCategoryWithDetails(categoryEntity, pageable);
            } else {
                // 카테고리가 존재하지 않으면 빈 결과
                aiServicesPage = Page.empty(pageable);
            }
        } else {
            // 🔥 전체 조회 시 카테고리 정보 포함
            aiServicesPage = aiServiceRepository.findAllWithCategory(pageable);
        }

        List<AiService> aiServices = aiServicesPage.getContent();

        // 🔥 배치로 키워드 조회하여 N+1 문제 해결
        List<Long> serviceIds = aiServices.stream()
                .map(AiService::getId)
                .collect(Collectors.toList());

        Map<Long, List<String>> keywordsMap = getKeywordsBatch(serviceIds);

        // 각 서비스에 해당하는 키워드 매핑
        List<List<String>> keywordsList = aiServices.stream()
                .map(service -> keywordsMap.getOrDefault(service.getId(), List.of()))
                .collect(Collectors.toList());

        return ServiceListResponse.from(aiServices, keywordsList);
    }

    /**
     * AI 서비스 상세 조회 - N+1 문제 해결
     */
    public ServiceDetailResponse getAiServiceDetail(Long serviceId) {
        log.debug("AI 서비스 상세 조회: serviceId={}", serviceId);

        // 🔥 카테고리 정보 포함하여 조회
        AiService aiService = aiServiceRepository.findByIdWithCategory(serviceId)
                .orElseThrow(() -> new BusinessException(AiServiceErrorCode.AI_SERVICE_NOT_FOUND));

        // 키워드 조회
        List<Keyword> keywords = aiServiceKeywordRepository.findKeywordsByAiService(aiService);

        // 리뷰 조회 (최신 순으로 제한)
        PageRequest reviewPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewsPage = reviewRepository.findByAiService(aiService, reviewPageable);
        List<Review> reviews = reviewsPage.getContent();

        return ServiceDetailResponse.from(aiService, keywords, reviews);
    }

    // ==================== 유틸리티 메소드들 ====================

    /**
     * 배치로 키워드 조회 (N+1 문제 해결)
     */
    private Map<Long, List<String>> getKeywordsBatch(List<Long> serviceIds) {
        if (serviceIds.isEmpty()) {
            return Map.of();
        }

        // 모든 서비스의 키워드를 한 번의 쿼리로 조회
        List<Object[]> keywordData = aiServiceKeywordRepository.findKeywordsByServiceIds(serviceIds);

        return keywordData.stream()
                .collect(Collectors.groupingBy(
                        data -> (Long) data[0], // service_id
                        Collectors.mapping(
                                data -> ((Keyword) data[1]).getName(), // keyword name
                                Collectors.toList()
                        )
                ));
    }

    /**
     * 페이지블 객체 생성
     */
    private Pageable createPageable(int page, int size, String sort) {
        Sort sortOrder = createSortOrder(sort);
        return PageRequest.of(page, size, sortOrder);
    }

    /**
     * 정렬 조건 생성 - 추천순/최신순만 지원
     */
    private Sort createSortOrder(String sort) {
        if (sort == null) sort = "rating"; // 기본값을 추천순으로 설정

        return switch (sort.toLowerCase()) {
            case "rating" -> {
                // 추천순: recommendation_score 기준 (높은 순)
                // recommendation_score가 null인 경우 평점으로 보조 정렬
                yield Sort.by(Sort.Direction.DESC, "recommendationScore")
                        .and(Sort.by(Sort.Direction.DESC, "averageRating"));
            }
            case "latest" -> {
                // 최신순: release_date 기준 (최신 순)
                yield Sort.by(Sort.Direction.DESC, "releaseDate");
            }
            default -> {
                // 기본값: 추천순
                yield Sort.by(Sort.Direction.DESC, "recommendationScore")
                        .and(Sort.by(Sort.Direction.DESC, "averageRating"));
            }
        };
    }


}