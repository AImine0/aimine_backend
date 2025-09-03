package com.aimine.aimine.review.repository;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.review.domain.Review;
import com.aimine.aimine.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // AI 서비스별 리뷰 목록 (페이징)
    Page<Review> findByAiService(AiService aiService, Pageable pageable);

    // 사용자별 리뷰 목록 (페이징)
    Page<Review> findByUser(User user, Pageable pageable);

    // 특정 사용자의 특정 서비스 리뷰 조회 (첫 번째만)
    Optional<Review> findFirstByUserAndAiServiceOrderByCreatedAtDesc(User user, AiService aiService);

    // 특정 사용자의 특정 서비스 모든 리뷰 조회
    List<Review> findByUserAndAiService(User user, AiService aiService);

    // 리뷰 존재 확인 - 중복 허용으로 변경되었으므로 개수 확인
    long countByUserAndAiService(User user, AiService aiService);

    // AI 서비스별 평균 평점 계산
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.aiService = :aiService")
    Optional<BigDecimal> findAverageRatingByAiService(@Param("aiService") AiService aiService);

    // AI 서비스별 리뷰 개수
    long countByAiService(AiService aiService);

    // 사용자별 리뷰 개수
    long countByUser(User user);

    // AI 서비스별 최신 리뷰 조회
    @Query("SELECT r FROM Review r WHERE r.aiService = :aiService ORDER BY r.createdAt DESC")
    Page<Review> findLatestReviewsByAiService(@Param("aiService") AiService aiService, Pageable pageable);

    // 평점별 리뷰 조회
    Page<Review> findByAiServiceAndRating(AiService aiService, Integer rating, Pageable pageable);
}