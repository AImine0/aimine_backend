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
        private String tags; // tags 필드 추가
    }

    // 정적 팩토리 메소드 - 완전히 수정된 버전
    public static BookmarkListResponse from(List<Bookmark> bookmarks) {
        List<BookmarkInfo> bookmarkInfos = bookmarks.stream()
                .map(bookmark -> {
                    String tags = bookmark.getAiService().getTags();
                    if (tags == null || tags.trim().isEmpty()) {
                        tags = bookmark.getAiService().getCategory().getDisplayName();
                    }

                    return BookmarkInfo.builder()
                            .id(bookmark.getId())
                            .aiServiceId(bookmark.getAiService().getId())
                            .serviceName(bookmark.getAiService().getName())
                            .serviceSummary(bookmark.getAiService().getDescription() != null ?
                                    bookmark.getAiService().getDescription() :
                                    bookmark.getAiService().getName() + " AI 서비스")
                            .logoUrl(bookmark.getAiService().getImagePath() != null ?
                                    bookmark.getAiService().getImagePath() :
                                    "/images/Logo/Logo_FINAL.svg")
                            .categoryDisplayName(bookmark.getAiService().getCategory().getDisplayName())
                            .pricingType(bookmark.getAiService().getPricingType().name())
                            .tags(tags)
                            .build();
                })
                .collect(Collectors.toList());

        return BookmarkListResponse.builder()
                .bookmarks(bookmarkInfos)
                .totalCount(bookmarkInfos.size())
                .build();
    }
}