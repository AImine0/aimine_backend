package com.aimine.aimine.review.service;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.aiservice.repository.AiServiceRepository;
import com.aimine.aimine.common.exception.BusinessException;
import com.aimine.aimine.common.exception.errorcode.AiServiceErrorCode;
import com.aimine.aimine.common.exception.errorcode.ReviewErrorCode;
import com.aimine.aimine.common.exception.errorcode.UserErrorCode;
import com.aimine.aimine.review.domain.Review;
import com.aimine.aimine.review.dto.ReviewCreateRequest;
import com.aimine.aimine.review.dto.ReviewCreateResponse;
import com.aimine.aimine.review.dto.ReviewDeleteResponse;
import com.aimine.aimine.review.repository.ReviewRepository;
import com.aimine.aimine.user.domain.User;
import com.aimine.aimine.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AiServiceRepository aiServiceRepository;

    /**
     * 리뷰 작성
     */
    @Transactional
    public ReviewCreateResponse createReview(Long userId, ReviewCreateRequest request) {
        log.debug("리뷰 작성 요청: userId={}, toolId={}, rating={}",
                userId, request.getToolId(), request.getRating());

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // AI 서비스 조회
        AiService aiService = aiServiceRepository.findById(request.getToolId())
                .orElseThrow(() -> new BusinessException(AiServiceErrorCode.AI_SERVICE_NOT_FOUND));

        // 이미 리뷰를 작성했는지 확인
        if (reviewRepository.existsByUserAndAiService(user, aiService)) {
            throw new BusinessException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
        }

        // 평점 유효성 검사
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new BusinessException(ReviewErrorCode.INVALID_RATING);
        }

        // 리뷰 생성 및 저장
        Review review = Review.builder()
                .user(user)
                .aiService(aiService)
                .rating(request.getRating())
                .content(request.getContent())
                .build();

        Review savedReview = reviewRepository.save(review);

        // AI 서비스 평균 평점 업데이트
        updateAiServiceRating(aiService);

        log.info("리뷰 작성 완료: reviewId={}, userId={}, toolId={}, rating={}",
                savedReview.getId(), userId, request.getToolId(), request.getRating());

        return ReviewCreateResponse.from(savedReview);
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public ReviewDeleteResponse deleteReview(Long userId, Long reviewId) {
        log.debug("리뷰 삭제 요청: userId={}, reviewId={}", userId, reviewId);

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 리뷰 조회
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ReviewErrorCode.REVIEW_NOT_FOUND));

        // 권한 확인 (본인의 리뷰인지)
        if (!review.isOwnedBy(user)) {
            throw new BusinessException(ReviewErrorCode.REVIEW_ACCESS_DENIED);
        }

        AiService aiService = review.getAiService();

        // 리뷰 삭제
        reviewRepository.delete(review);

        // AI 서비스 평균 평점 업데이트
        updateAiServiceRating(aiService);

        log.info("리뷰 삭제 완료: reviewId={}, userId={}", reviewId, userId);

        return ReviewDeleteResponse.success();
    }

    /**
     * AI 서비스의 평균 평점 업데이트
     */
    private void updateAiServiceRating(AiService aiService) {
        // 평균 평점 계산
        BigDecimal averageRating = reviewRepository.findAverageRatingByAiService(aiService)
                .orElse(BigDecimal.ZERO);

        // 리뷰 개수 조회
        long totalReviews = reviewRepository.countByAiService(aiService);

        // 소수점 둘째 자리까지 반올림
        averageRating = averageRating.setScale(2, RoundingMode.HALF_UP);

        // AI 서비스 평점 정보 업데이트
        aiService.updateRating(averageRating, (int) totalReviews);
        aiServiceRepository.save(aiService);

        log.debug("AI 서비스 평점 업데이트: serviceId={}, averageRating={}, totalReviews={}",
                aiService.getId(), averageRating, totalReviews);
    }

    /**
     * 사용자가 특정 AI 서비스에 리뷰를 작성했는지 확인
     */
    public boolean hasUserReviewed(Long userId, Long serviceId) {
        User user = userRepository.findById(userId).orElse(null);
        AiService aiService = aiServiceRepository.findById(serviceId).orElse(null);

        if (user == null || aiService == null) {
            return false;
        }

        return reviewRepository.existsByUserAndAiService(user, aiService);
    }

    /**
     * 사용자의 리뷰 개수 조회
     */
    public long getUserReviewCount(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return 0;
        }

        return reviewRepository.countByUser(user);
    }
}