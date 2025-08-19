package com.aimine.aimine.keyword.dto;

import com.aimine.aimine.keyword.domain.Keyword;
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
public class KeywordByTypeResponse {

    private List<KeywordInfo> keywords;
    private Integer totalCount;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordInfo {
        private Long id;
        private String keyword;
        private String type;
        private Long toolCount;
    }

    // 정적 팩토리 메소드
    public static KeywordByTypeResponse from(List<Keyword> keywords, List<Long> toolCounts) {
        List<KeywordInfo> keywordInfos = keywords.stream()
                .map(keyword -> {
                    int index = keywords.indexOf(keyword);
                    Long toolCount = toolCounts.size() > index ? toolCounts.get(index) : 0L;

                    return KeywordInfo.builder()
                            .id(keyword.getId())
                            .keyword(keyword.getName())
                            .type(keyword.getType().name())
                            .toolCount(toolCount)
                            .build();
                })
                .collect(Collectors.toList());

        return KeywordByTypeResponse.builder()
                .keywords(keywordInfos)
                .totalCount(keywordInfos.size())
                .build();
    }
}