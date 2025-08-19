package com.aimine.aimine.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 생성 요청")
public class ReviewCreateRequest {

    @NotNull(message = "AI 서비스 ID는 필수입니다")
    @Schema(description = "AI 서비스 ID", example = "1")
    private Long toolId;

    @NotNull(message = "평점은 필수입니다")
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다")
    @Schema(description = "평점 (1-5)", example = "4")
    private Integer rating;

    @Schema(description = "리뷰 내용", example = "정말 유용한 AI 도구입니다. 업무 효율이 크게 향상되었어요!")
    private String content;
}