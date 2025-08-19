package com.aimine.aimine.bookmark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "북마크 생성 요청")
public class BookmarkCreateRequest {

    @NotNull(message = "AI 서비스 ID는 필수입니다")
    @Schema(description = "AI 서비스 ID", example = "1")
    private Long aiServiceId;
}