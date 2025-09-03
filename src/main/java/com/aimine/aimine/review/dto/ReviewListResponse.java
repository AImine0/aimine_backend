package com.aimine.aimine.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResponse {

    @JsonProperty("reviews")
    private List<ReviewInfo> reviews;

    @JsonProperty("total_count")
    private Integer totalCount;

    @JsonProperty("average_rating")
    private Double averageRating;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewInfo {
        private Long id;

        @JsonProperty("user_nickname")
        private String userNickname;

        private Integer rating;
        private String content;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;
    }
}