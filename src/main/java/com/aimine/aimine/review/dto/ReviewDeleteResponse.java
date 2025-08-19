package com.aimine.aimine.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDeleteResponse {

    private boolean success;
    private String message;

    // 정적 팩토리 메소드
    public static ReviewDeleteResponse success() {
        return ReviewDeleteResponse.builder()
                .success(true)
                .message("리뷰가 삭제되었습니다")
                .build();
    }
}