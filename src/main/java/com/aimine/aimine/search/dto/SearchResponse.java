package com.aimine.aimine.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    private String query;
    private Integer totalCount;
    private List<AiServiceInfo> tools;
    private List<String> suggestedKeywords;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiServiceInfo {
        private Long id;
        private String serviceName;
        private String description;
        private String logoUrl;
        private String categoryName;
        private String pricingType;
        private BigDecimal overallRating;
        private List<String> keywords;
    }
}