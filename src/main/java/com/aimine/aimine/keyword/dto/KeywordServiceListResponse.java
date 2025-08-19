package com.aimine.aimine.keyword.dto;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.keyword.domain.Keyword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordServiceListResponse {

    private KeywordInfo keyword;
    private List<ToolInfo> tools;
    private Integer totalCount;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordInfo {
        private Long id;
        private String keyword;
        private String type;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolInfo {
        private Long id;
        private String serviceName;
        private String description;
        private String logoUrl;
        private String categoryName;
        private String pricingType;
        private BigDecimal overallRating;
        private Long bookmarkCount;
    }

    // 정적 팩토리 메소드
    public static KeywordServiceListResponse from(Keyword keyword, List<AiService> aiServices, List<Long> bookmarkCounts) {
        KeywordInfo keywordInfo = KeywordInfo.builder()
                .id(keyword.getId())
                .keyword(keyword.getName())
                .type(keyword.getType().name())
                .build();

        List<ToolInfo> toolInfos = aiServices.stream()
                .map(service -> {
                    int index = aiServices.indexOf(service);
                    Long bookmarkCount = bookmarkCounts.size() > index ? bookmarkCounts.get(index) : 0L;

                    return ToolInfo.builder()
                            .id(service.getId())
                            .serviceName(service.getName())
                            .description(service.getOfficialUrl()) // 임시로 URL 사용
                            .logoUrl("https://example.com/" + service.getName().toLowerCase() + "-logo.png") // 임시 로고 URL
                            .categoryName(service.getCategory().getDisplayName())
                            .pricingType(service.getPricingType().name())
                            .overallRating(service.getAverageRating())
                            .bookmarkCount(bookmarkCount)
                            .build();
                })
                .collect(Collectors.toList());

        return KeywordServiceListResponse.builder()
                .keyword(keywordInfo)
                .tools(toolInfos)
                .totalCount(toolInfos.size())
                .build();
    }
}