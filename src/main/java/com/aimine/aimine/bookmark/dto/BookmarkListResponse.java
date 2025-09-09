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
        private String tags;
        private String websiteUrl;
    }

    // 이미지 URL을 안전하게 생성하는 헬퍼 메서드
    private static String buildImageUrl(String baseUrl, String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return baseUrl + "/images/Logo/Logo_FINAL.svg"; // 기본 이미지도 전체 URL로
        }
        // 이미 완전한 URL인 경우 그대로 반환
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath;
        }
        // 상대 경로인 경우 baseUrl 추가
        return baseUrl + (imagePath.startsWith("/") ? imagePath : "/" + imagePath);
    }

    // 정적 팩토리 메소드 - officialUrl을 websiteUrl로 매핑
    public static BookmarkListResponse from(List<Bookmark> bookmarks) {
        String baseUrl = "https://aimine.up.railway.app"; // API 서버 주소

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
                            // 모든 이미지 경로에 baseUrl 적용
                            .logoUrl(buildImageUrl(baseUrl, bookmark.getAiService().getImagePath()))
                            .categoryDisplayName(bookmark.getAiService().getCategory().getDisplayName())
                            .pricingType(bookmark.getAiService().getPricingType().name())
                            .tags(tags)
                            .websiteUrl(bookmark.getAiService().getOfficialUrl())
                            .build();
                })
                .collect(Collectors.toList());

        return BookmarkListResponse.builder()
                .bookmarks(bookmarkInfos)
                .totalCount(bookmarkInfos.size())
                .build();
    }
}