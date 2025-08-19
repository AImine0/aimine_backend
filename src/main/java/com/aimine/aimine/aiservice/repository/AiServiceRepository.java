package com.aimine.aimine.aiservice.repository;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.category.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AiServiceRepository extends JpaRepository<AiService, Long> {


    long countByCategory(Category category);


    // 이름으로 검색
    @Query("SELECT a FROM AiService a WHERE a.name LIKE %:name%")
    Page<AiService> findByNameContaining(@Param("name") String name, Pageable pageable);

    // 가격 타입별 조회
    Page<AiService> findByPricingType(AiService.PricingType pricingType, Pageable pageable);

    // 추천 순위별 조회 (상위 N개)
    @Query("SELECT a FROM AiService a WHERE a.recommendationRank IS NOT NULL ORDER BY a.recommendationRank ASC")
    List<AiService> findTopByRecommendationRank(Pageable pageable);

    // 평점순 조회
    @Query("SELECT a FROM AiService a ORDER BY a.averageRating DESC, a.totalReviews DESC")
    Page<AiService> findAllOrderByRating(Pageable pageable);

    // 최신순 조회
    @Query("SELECT a FROM AiService a ORDER BY a.releaseDate DESC NULLS LAST")
    Page<AiService> findAllOrderByLatest(Pageable pageable);

    // 복합 검색 (이름, 카테고리, 가격타입)
    @Query("SELECT a FROM AiService a WHERE " +
            "(:name IS NULL OR a.name LIKE %:name%) AND " +
            "(:category IS NULL OR a.category = :category) AND " +
            "(:pricingType IS NULL OR a.pricingType = :pricingType)")
    Page<AiService> findBySearchCriteria(
            @Param("name") String name,
            @Param("category") Category category,
            @Param("pricingType") AiService.PricingType pricingType,
            Pageable pageable
    );


    // 검색을 위한 추가 메소드들
    Page<AiService> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<AiService> findByCategoryDisplayName(String categoryDisplayName, Pageable pageable);

    // 카테고리별 조회
    Page<AiService> findByCategory(Category category, Pageable pageable);

    Page<AiService> findByCategoryDisplayNameAndPricingType(
            String categoryDisplayName,
            AiService.PricingType pricingType,
            Pageable pageable
    );

    Page<AiService> findByNameContainingIgnoreCaseAndCategoryDisplayName(
            String name,
            String categoryDisplayName,
            Pageable pageable
    );

    Page<AiService> findByNameContainingIgnoreCaseAndPricingType(
            String name,
            AiService.PricingType pricingType,
            Pageable pageable
    );

    Page<AiService> findByNameContainingIgnoreCaseAndCategoryDisplayNameAndPricingType(
            String name,
            String categoryDisplayName,
            AiService.PricingType pricingType,
            Pageable pageable
    );
}