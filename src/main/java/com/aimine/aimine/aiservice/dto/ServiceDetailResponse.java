package com.aimine.aimine.aiservice.dto;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.keyword.domain.Keyword;
import com.aimine.aimine.review.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDetailResponse {

    private boolean success;
    private ServiceDetailData data;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceDetailData {
        private Long id;
        private String serviceName;
        private String description;
        private String websiteUrl;
        private String logoUrl;
        private LocalDate launchDate;
        private CategoryInfo category;
        private String pricingType;
        private BigDecimal overallRating;
        private List<KeywordInfo> keywords;
        private List<ReviewInfo> reviews;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long id;
        private String name;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordInfo {
        private Long id;
        private String keyword;
        private String type;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewInfo {
        private Long id;
        private UserInfo user;
        private BigDecimal rating;
        private String content;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String nickname;
    }

    // 정적 팩토리 메소드
    // ServiceDetailResponse.java의 from 메소드 수정 부분

    public static ServiceDetailResponse from(AiService aiService, List<Keyword> keywords, List<Review> reviews) {
        List<KeywordInfo> keywordInfos = keywords.stream()
                .map(keyword -> KeywordInfo.builder()
                        .id(keyword.getId())
                        .keyword(keyword.getName())
                        .type(keyword.getType().name())
                        .build())
                .collect(Collectors.toList());

        List<ReviewInfo> reviewInfos = reviews.stream()
                .map(review -> ReviewInfo.builder()
                        .id(review.getId())
                        .user(UserInfo.builder()
                                .nickname(review.getUser().getName()) // 닉네임으로 사용
                                .build())
                        .rating(BigDecimal.valueOf(review.getRating()))
                        .content(review.getContent())
                        .createdAt(review.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        ServiceDetailData data = ServiceDetailData.builder()
                .id(aiService.getId())
                .serviceName(aiService.getName())
                .description(aiService.getDescription() != null ? aiService.getDescription() : "AI 서비스 상세 설명")
                .websiteUrl(aiService.getOfficialUrl())
                .logoUrl(aiService.getServiceImagePath()) // 상세 페이지용 이미지 경로 사용
                .launchDate(aiService.getReleaseDate())
                .category(CategoryInfo.builder()
                        .id(aiService.getCategory().getId())
                        .name(aiService.getCategory().getDisplayName())
                        .build())
                .pricingType(aiService.getPricingType().name())
                .overallRating(aiService.getAverageRating())
                .keywords(keywordInfos)
                .reviews(reviewInfos)
                .build();

        return ServiceDetailResponse.builder()
                .success(true)
                .data(data)
                .build();
    }
}