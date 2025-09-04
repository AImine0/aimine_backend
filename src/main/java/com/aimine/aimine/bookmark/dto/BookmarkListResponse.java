package com.aimine.aimine.bookmark.dto;

import com.aimine.aimine.bookmark.domain.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkListResponse {

    private List<BookmarkInfo> bookmarks;
    private Integer totalCount;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkInfo {
        private Long id;
        private Long aiServiceId;
        private String serviceName;
        private String serviceSummary;
        private String logoUrl;
        private String categoryDisplayName;
        private String pricingType;
    }

    // 정적 팩토리 메소드
    public static BookmarkListResponse from(List<Bookmark> bookmarks) {
        List<BookmarkInfo> bookmarkInfos = bookmarks.stream()
                .map(bookmark -> BookmarkInfo.builder()
                        .id(bookmark.getId())
                        .aiServiceId(bookmark.getAiService().getId())
                        .serviceName(bookmark.getAiService().getName())
                        .serviceSummary(bookmark.getAiService().getDescription()) // ✅ 실제 description 사용
                        .logoUrl(bookmark.getAiService().getImagePath()) // ✅ DB의 실제 이미지 경로 사용
                        .categoryDisplayName(bookmark.getAiService().getCategory().getDisplayName())
                        .pricingType(bookmark.getAiService().getPricingType().name().toLowerCase())
                        .build())
                .collect(Collectors.toList());

        return BookmarkListResponse.builder()
                .bookmarks(bookmarkInfos)
                .totalCount(bookmarkInfos.size())
                .build();
    }
}