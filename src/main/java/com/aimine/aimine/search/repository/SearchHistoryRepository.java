package com.aimine.aimine.search.repository;

import com.aimine.aimine.search.domain.SearchHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    /**
     * 사용자별 최근 검색어 조회 (중복 제거, 최신순)
     */
    @Query("SELECT s.searchQuery FROM SearchHistory s " +
            "WHERE s.user.id = :userId " +
            "GROUP BY s.searchQuery " +
            "ORDER BY MAX(s.createdAt) DESC")
    List<String> findRecentSearchQueriesByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 특정 사용자의 특정 검색어 존재 여부 확인 (중복 방지용)
     */
    Optional<SearchHistory> findByUserIdAndSearchQuery(Long userId, String searchQuery);

    /**
     * 사용자별 특정 검색어 삭제
     */
    @Modifying
    @Query("DELETE FROM SearchHistory s WHERE s.user.id = :userId AND s.searchQuery = :query")
    void deleteByUserIdAndSearchQuery(@Param("userId") Long userId, @Param("query") String query);

    /**
     * 사용자의 모든 검색어 이력 삭제
     */
    @Modifying
    @Query("DELETE FROM SearchHistory s WHERE s.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    /**
     * 사용자별 검색어 개수 조회
     */
    @Query("SELECT COUNT(DISTINCT s.searchQuery) FROM SearchHistory s WHERE s.user.id = :userId")
    Long countDistinctByUserId(@Param("userId") Long userId);
}