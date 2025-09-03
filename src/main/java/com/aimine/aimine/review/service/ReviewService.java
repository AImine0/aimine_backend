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
import com.aimine.aimine.review.dto.ReviewListResponse;
import com.aimine.aimine.review.repository.ReviewRepository;
import com.aimine.aimine.user.domain.User;
import com.aimine.aimine.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AiServiceRepository aiServiceRepository;

    /**
     * 리뷰 목록 조회 (AI 서비스별 또는 전체)
     */
    public ReviewListResponse getReviews(Long serviceId) {
        log.debug("리뷰 목록 조회: serviceId={}", serviceId);

        List<Review> reviews;
        double averageRating = 0.0;

        if (serviceId != null) {
            // 특정 AI 서비스의 리뷰 조회
            AiService aiService = aiServiceRepository.findById(serviceId)
                    .orElseThrow(() -> new BusinessException(AiServiceErrorCode.AI_SERVICE_NOT_FOUND));

            reviews = reviewRepository.findByAiService(aiService, Pageable.unpaged()).getContent();

            // 해당 서비스의 평균 평점 계산
            averageRating = reviewRepository.findAverageRatingByAiService(aiService)
                    .map(BigDecimal::doubleValue)
                    .orElse(0.0);

        } else {
            // 전체 리뷰 조회
            reviews = reviewRepository.findAll();

            // 전체 평균 평점 계산
            if (!reviews.isEmpty()) {
                averageRating = reviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0);
            }
        }

        // Review 엔티티를 ReviewInfo DTO로 변환
        List<ReviewListResponse.ReviewInfo> reviewInfos = reviews.stream()
                .map(review -> ReviewListResponse.ReviewInfo.builder()
                        .id(review.getId())
                        .userNickname(review.getUser().getName()) // User의 name을 nickname으로 사용
                        .rating(review.getRating())
                        .content(review.getContent())
                        .createdAt(review.getCreatedAt())
                        .updatedAt(review.getCreatedAt()) // Review에는 updatedAt이 없으므로 createdAt 사용
                        .build())
                .collect(Collectors.toList());

        // 응답 DTO 생성
        ReviewListResponse response = ReviewListResponse.builder()
                .reviews(reviewInfos)
                .totalCount(reviewInfos.size())
                .averageRating(Math.round(averageRating * 100.0) / 100.0) // 소수점 2자리까지
                .build();

        log.info("리뷰 목록 조회 완료: serviceId={}, totalCount={}, averageRating={}",
                serviceId, response.getTotalCount(), response.getAverageRating());

        return response;
    }

    /**
     * 리뷰 작성 - 중복 확인 제거, 같은 사용자가 같은 서비스에 여러 리뷰 작성 가능
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

        // 중복 리뷰 확인 로직 제거 - 같은 사용자가 같은 서비스에 여러 리뷰 작성 가능

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
     * 중복 리뷰 허용으로 변경되었으므로 리뷰 존재 여부만 확인
     */
    public boolean hasUserReviewed(Long userId, Long serviceId) {
        User user = userRepository.findById(userId).orElse(null);
        AiService aiService = aiServiceRepository.findById(serviceId).orElse(null);

        if (user == null || aiService == null) {
            return false;
        }

        // 해당 사용자가 해당 서비스에 대한 리뷰가 하나라도 있는지 확인
        return reviewRepository.countByUserAndAiService(user, aiService) > 0;
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

    /**
     * 특정 사용자가 특정 서비스에 작성한 리뷰 개수 조회
     */
    public long getUserServiceReviewCount(Long userId, Long serviceId) {
        User user = userRepository.findById(userId).orElse(null);
        AiService aiService = aiServiceRepository.findById(serviceId).orElse(null);

        if (user == null || aiService == null) {
            return 0;
        }

        return reviewRepository.countByUserAndAiService(user, aiService);
    }

    /**
     * 특정 사용자가 특정 서비스에 작성한 모든 리뷰 조회
     */
    public List<Review> getUserServiceReviews(Long userId, Long serviceId) {
        User user = userRepository.findById(userId).orElse(null);
        AiService aiService = aiServiceRepository.findById(serviceId).orElse(null);

        if (user == null || aiService == null) {
            return List.of();
        }

        return reviewRepository.findByUserAndAiService(user, aiService);
    }
}