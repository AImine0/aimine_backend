package com.aimine.aimine.review.dto;

import com.aimine.aimine.review.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateResponse {

    private boolean success;
    private String message;
    private ReviewInfo review;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewInfo {
        private Long id;
        private Long userId;
        private Long toolId;
        private Integer rating;
        private String content;
        private LocalDateTime createdAt;
    }

    // 정적 팩토리 메소드
    public static ReviewCreateResponse from(Review review) {
        ReviewInfo reviewInfo = ReviewInfo.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .toolId(review.getAiService().getId())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();

        return ReviewCreateResponse.builder()
                .success(true)
                .message("리뷰가 작성되었습니다")
                .review(reviewInfo)
                .build();
    }
}