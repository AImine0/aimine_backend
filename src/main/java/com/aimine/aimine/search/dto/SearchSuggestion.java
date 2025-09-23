package com.aimine.aimine.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchSuggestion {
    private SuggestionType type;
    private Long id;
    private String text;
    private String logoUrl;
    private String tag;
    private String categoryPath;

    public enum SuggestionType {
        AI_SERVICE,    // AI 서비스
        CATEGORY,      // 기능별/직업별 카테고리  
        KEYWORD        // 기능 키워드
    }
}