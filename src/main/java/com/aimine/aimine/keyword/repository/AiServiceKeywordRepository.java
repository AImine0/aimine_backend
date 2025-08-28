package com.aimine.aimine.keyword.repository;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.keyword.domain.AiServiceKeyword;
import com.aimine.aimine.keyword.domain.Keyword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiServiceKeywordRepository extends JpaRepository<AiServiceKeyword, Long> {

    // ==================== 기존 메소드들 유지 ====================

    // 특정 AI 서비스의 키워드 목록
    @Query("SELECT ask.keyword FROM AiServiceKeyword ask WHERE ask.aiService = :aiService")
    List<Keyword> findKeywordsByAiService(@Param("aiService") AiService aiService);

    // 특정 키워드를 가진 AI 서비스 목록
    @Query("SELECT ask.aiService FROM AiServiceKeyword ask WHERE ask.keyword = :keyword")
    List<AiService> findAiServicesByKeyword(@Param("keyword") Keyword keyword);

    // 키워드별 AI 서비스 개수
    @Query("SELECT COUNT(ask) FROM AiServiceKeyword ask WHERE ask.keyword = :keyword")
    Long countAiServicesByKeyword(@Param("keyword") Keyword keyword);

    // AI 서비스와 키워드 연결 삭제
    void deleteByAiServiceAndKeyword(AiService aiService, Keyword keyword);

    // AI 서비스의 모든 키워드 연결 삭제
    void deleteByAiService(AiService aiService);

    // ==================== N+1 최적화를 위한 추가 메소드들 ====================

    /**
     * 여러 AI 서비스의 키워드를 배치로 조회 (N+1 문제 해결)
     * 반환: Object[] = {Long serviceId, Keyword keyword}
     */
    @Query("SELECT ask.aiService.id, ask.keyword FROM AiServiceKeyword ask " +
            "WHERE ask.aiService.id IN :serviceIds " +
            "ORDER BY ask.aiService.id, ask.keyword.name")
    List<Object[]> findKeywordsByServiceIds(@Param("serviceIds") List<Long> serviceIds);

    /**
     * AI 서비스 ID로 키워드 조회 (성능 최적화)
     */
    @Query("SELECT k FROM AiServiceKeyword ask " +
            "JOIN ask.keyword k " +
            "WHERE ask.aiService.id = :serviceId " +
            "ORDER BY k.name")
    List<Keyword> findKeywordsByAiServiceId(@Param("serviceId") Long serviceId);

    /**
     * 키워드별 AI 서비스 조회 - 카테고리 정보 포함 (N+1 해결)
     */
    @Query("SELECT DISTINCT ask.aiService FROM AiServiceKeyword ask " +
            "JOIN FETCH ask.aiService.category " +
            "WHERE ask.keyword.id = :keywordId")
    Page<AiService> findAiServicesWithCategoryByKeywordId(
            @Param("keywordId") Long keywordId,
            Pageable pageable);

    /**
     * 인기 키워드 조회 (AI 서비스 개수 기준)
     */
    @Query("SELECT k, COUNT(ask) as cnt FROM AiServiceKeyword ask " +
            "JOIN ask.keyword k " +
            "GROUP BY k " +
            "ORDER BY cnt DESC")
    List<Object[]> findPopularKeywords(Pageable pageable);

    /**
     * 특정 카테고리의 키워드 조회
     */
    @Query("SELECT DISTINCT k FROM AiServiceKeyword ask " +
            "JOIN ask.keyword k " +
            "WHERE ask.aiService.category.id = :categoryId " +
            "ORDER BY k.name")
    List<Keyword> findKeywordsByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * AI 서비스의 키워드 개수
     */
    @Query("SELECT COUNT(ask) FROM AiServiceKeyword ask WHERE ask.aiService.id = :serviceId")
    Long countKeywordsByServiceId(@Param("serviceId") Long serviceId);

    /**
     * 키워드 통계 - 카테고리별 키워드 분포
     */
    @Query("SELECT c.name, k.name, COUNT(ask) FROM AiServiceKeyword ask " +
            "JOIN ask.aiService.category c " +
            "JOIN ask.keyword k " +
            "GROUP BY c.name, k.name " +
            "ORDER BY c.name, COUNT(ask) DESC")
    List<Object[]> findKeywordStatsByCategory();
}