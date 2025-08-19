package com.aimine.aimine.bookmark.repository;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.bookmark.domain.Bookmark;
import com.aimine.aimine.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // 사용자별 북마크 목록 (페이징)
    Page<Bookmark> findByUser(User user, Pageable pageable);

    // 특정 사용자의 특정 서비스 북마크 조회
    Optional<Bookmark> findByUserAndAiService(User user, AiService aiService);

    // 북마크 존재 확인
    boolean existsByUserAndAiService(User user, AiService aiService);

    // 사용자별 북마크 개수
    long countByUser(User user);

    // AI 서비스별 북마크 개수
    long countByAiService(AiService aiService);

    // 사용자의 북마크된 AI 서비스 목록 (조인 쿼리)
    @Query("SELECT b.aiService FROM Bookmark b WHERE b.user = :user")
    Page<AiService> findAiServicesByUser(@Param("user") User user, Pageable pageable);
}