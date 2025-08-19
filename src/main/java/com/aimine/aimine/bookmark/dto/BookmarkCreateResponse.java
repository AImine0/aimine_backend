package com.aimine.aimine.bookmark.dto;

import com.aimine.aimine.bookmark.domain.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkCreateResponse {

    private boolean success;
    private String message;
    private BookmarkInfo bookmark;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkInfo {
        private Long userId;
        private Long toolId;
    }

    // 정적 팩토리 메소드
    public static BookmarkCreateResponse from(Bookmark bookmark) {
        BookmarkInfo bookmarkInfo = BookmarkInfo.builder()
                .userId(bookmark.getUser().getId())
                .toolId(bookmark.getAiService().getId())
                .build();

        return BookmarkCreateResponse.builder()
                .success(true)
                .message("북마크가 추가되었습니다")
                .bookmark(bookmarkInfo)
                .build();
    }
}