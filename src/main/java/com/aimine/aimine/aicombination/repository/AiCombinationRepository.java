package com.aimine.aimine.aicombination.repository;

import com.aimine.aimine.aicombination.domain.AiCombination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiCombinationRepository extends JpaRepository<AiCombination, Long> {

    // 카테고리별 조회
    Page<AiCombination> findByCategory(String category, Pageable pageable);

    // 카테고리별 조회 (null 허용)
    @Query("SELECT ac FROM AiCombination ac WHERE " +
            "(:category IS NULL OR ac.category = :category)")
    Page<AiCombination> findByCategoryWithNull(@Param("category") String category, Pageable pageable);

    // 이름으로 검색
    @Query("SELECT ac FROM AiCombination ac WHERE ac.name LIKE %:name%")
    Page<AiCombination> findByNameContaining(@Param("name") String name, Pageable pageable);

    // 전체 카테고리 목록 조회
    @Query("SELECT DISTINCT ac.category FROM AiCombination ac WHERE ac.category IS NOT NULL ORDER BY ac.category")
    List<String> findDistinctCategories();

    // 추천 조합 조회 (featured 기능이 필요하다면)
    @Query("SELECT ac FROM AiCombination ac ORDER BY ac.id ASC")
    Page<AiCombination> findFeaturedCombinations(Pageable pageable);
}