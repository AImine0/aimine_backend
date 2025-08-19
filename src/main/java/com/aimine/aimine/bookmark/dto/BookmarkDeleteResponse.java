package com.aimine.aimine.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDeleteResponse {

    private boolean success;
    private String message;

    // 정적 팩토리 메소드
    public static BookmarkDeleteResponse success() {
        return BookmarkDeleteResponse.builder()
                .success(true)
                .message("북마크가 제거되었습니다")
                .build();
    }
}