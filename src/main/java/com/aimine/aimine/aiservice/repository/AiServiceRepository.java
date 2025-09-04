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
import java.util.Optional;

@Repository
public interface AiServiceRepository extends JpaRepository<AiService, Long> {

    // ==================== 기존 메소드들 유지 ====================
    long countByCategory(Category category);

    // 이름으로 검색
    @Query("SELECT a FROM AiService a WHERE a.name LIKE %:name%")
    Page<AiService> findByNameContaining(@Param("name") String name, Pageable pageable);

    // 가격 타입별 조회 - 기존 메소드 유지하되 최적화 버전도 추가
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

    // 추천순 조회 최적화 (recommendation_score 기준)
    @Query("SELECT DISTINCT a FROM AiService a " +
            "LEFT JOIN FETCH a.category " +
            "ORDER BY a.recommendationScore DESC NULLS LAST, a.averageRating DESC")
    Page<AiService> findAllOrderByRecommendationWithCategory(Pageable pageable);

    // ==================== SearchService에서 필요한 메소드들 ====================

    // 이름으로 검색 (대소문자 무시)
    @Query("SELECT DISTINCT a FROM AiService a " +
            "LEFT JOIN FETCH a.category " +
            "WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<AiService> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    // 카테고리 표시명으로 검색
    @Query("SELECT DISTINCT a FROM AiService a " +
            "LEFT JOIN FETCH a.category c " +
            "WHERE c.displayName = :categoryDisplayName")
    Page<AiService> findByCategoryDisplayName(@Param("categoryDisplayName") String categoryDisplayName, Pageable pageable);

    // 카테고리 표시명 + 가격 타입
    @Query("SELECT DISTINCT a FROM AiService a " +
            "LEFT JOIN FETCH a.category c " +
            "WHERE c.displayName = :categoryDisplayName AND a.pricingType = :pricingType")
    Page<AiService> findByCategoryDisplayNameAndPricingType(
            @Param("categoryDisplayName") String categoryDisplayName,
            @Param("pricingType") AiService.PricingType pricingType,
            Pageable pageable);

    // 이름 + 카테고리 표시명
    @Query("SELECT DISTINCT a FROM AiService a " +
            "LEFT JOIN FETCH a.category c " +
            "WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.displayName = :categoryDisplayName")
    Page<AiService> findByNameContainingIgnoreCaseAndCategoryDisplayName(
            @Param("name") String name,
            @Param("categoryDisplayName") String categoryDisplayName,
            Pageable pageable);

    // 이름 + 가격 타입
    @Query("SELECT DISTINCT a FROM AiService a " +
            "LEFT JOIN FETCH a.category " +
            "WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')) AND a.pricingType = :pricingType")
    Page<AiService> findByNameContainingIgnoreCaseAndPricingType(
            @Param("name") String name,
            @Param("pricingType") AiService.PricingType pricingType,
            Pageable pageable);

    // 이름 + 카테고리 표시명 + 가격 타입 (모든 조건)
    @Query("SELECT DISTINCT a FROM AiService a " +
            "LEFT JOIN FETCH a.category c " +
            "WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND c.displayName = :categoryDisplayName " +
            "AND a.pricingType = :pricingType")
    Page<AiService> findByNameContainingIgnoreCaseAndCategoryDisplayNameAndPricingType(
            @Param("name") String name,
            @Param("categoryDisplayName") String categoryDisplayName,
            @Param("pricingType") AiService.PricingType pricingType,
            Pageable pageable);

    // ==================== N+1 최적화된 메소드들 ====================

    // 기본 findAll을 카테고리 정보 포함으로 최적화
    @Query("SELECT DISTINCT a FROM AiService a LEFT JOIN FETCH a.category")
    Page<AiService> findAllWithCategory(Pageable pageable);

    // ID로 조회 시 카테고리 정보 포함
    @Query("SELECT a FROM AiService a LEFT JOIN FETCH a.category WHERE a.id = :id")
    Optional<AiService> findByIdWithCategory(@Param("id") Long id);

    // 카테고리별 조회 최적화
    @Query("SELECT DISTINCT a FROM AiService a " +
            "LEFT JOIN FETCH a.category c " +
            "WHERE c = :category")
    Page<AiService> findByCategoryWithDetails(@Param("category") Category category, Pageable pageable);

    // 가격 타입별 조회 최적화
    @Query("SELECT DISTINCT a FROM AiService a " +
            "LEFT JOIN FETCH a.category " +
            "WHERE a.pricingType = :pricingType")
    Page<AiService> findByPricingTypeWithCategory(
            @Param("pricingType") AiService.PricingType pricingType,
            Pageable pageable);

    // 평점순 조회 최적화
    @Query("SELECT DISTINCT a FROM AiService a " +
            "LEFT JOIN FETCH a.category " +
            "ORDER BY a.averageRating DESC, a.totalReviews DESC")
    Page<AiService> findAllOrderByRatingWithCategory(Pageable pageable);

    // 최신순 조회 최적화
    @Query("SELECT DISTINCT a FROM AiService a " +
            "LEFT JOIN FETCH a.category " +
            "ORDER BY a.releaseDate DESC NULLS LAST")
    Page<AiService> findAllOrderByLatestWithCategory(Pageable pageable);

    // 추천순 조회 최적화
    @Query("SELECT DISTINCT a FROM AiService a " +
            "LEFT JOIN FETCH a.category " +
            "WHERE a.recommendationRank IS NOT NULL " +
            "ORDER BY a.recommendationRank ASC")
    Page<AiService> findTopRecommendedWithCategory(Pageable pageable);
}