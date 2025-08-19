package com.aimine.aimine.common.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortedBy {
    // AI 서비스 정렬 기준
    RATING("rating", "평점순"),
    LATEST("latest", "최신순"),
    NAME("name", "이름순"),
    RECOMMENDATION("recommendation", "추천순"),
    BOOKMARK_COUNT("bookmarkCount", "북마크순"),

    // 리뷰 정렬 기준
    CREATED_AT("createdAt", "작성일순");

    private final String value;
    private final String description;

    public static SortedBy fromString(String value) {
        if (value == null) {
            return RATING; // 기본값
        }

        for (SortedBy sortedBy : SortedBy.values()) {
            if (sortedBy.value.equalsIgnoreCase(value)) {
                return sortedBy;
            }
        }

        return RATING; // 기본값
    }
}