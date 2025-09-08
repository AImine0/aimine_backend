package com.aimine.aimine.aicombination.dto;

import com.aimine.aimine.aicombination.domain.AiCombination;
import com.aimine.aimine.aiservice.domain.AiService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCombinationListResponse {

    private List<CombinationInfo> combinations;
    private Integer totalCount;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CombinationInfo {
        private Long id;
        private String title;
        private String description;
        private String category;
        private Boolean isFeatured;
        private List<AiServiceInfo> aiServices;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiServiceInfo {
        private Long id;
        private String name;
        private String purpose; // 기존 필드 유지

        private String description;    // AI 서비스 상세 설명
        private String tags;          // DB tags 컬럼 내용 ("AI 챗봇" 등)
        private String logoUrl;       // 로고 이미지 URL (image_path)
        private String websiteUrl;    // 공식 웹사이트 URL
        private BigDecimal overallRating; // 평점
        private String categoryName;  // 카테고리명
    }

    public static AiCombinationListResponse from(
            List<AiCombination> combinations,
            Map<Long, List<AiService>> combinationServicesMap
    ) {
        List<CombinationInfo> combinationInfos = combinations.stream()
                .map(combination -> {
                    List<AiService> aiServices = combinationServicesMap.getOrDefault(
                            combination.getId(), List.of()
                    );

                    List<AiServiceInfo> aiServiceInfos = aiServices.stream()
                            .map(service -> AiServiceInfo.builder()
                                    .id(service.getId())
                                    .name(service.getName())
                                    .description(service.getDescription() != null ?
                                            service.getDescription() : "AI 서비스 설명")
                                    .logoUrl(service.getImagePath())
                                    .categoryName(service.getCategory().getDisplayName())
                                    .tags(service.getTags() != null ? service.getTags() : "")
                                    .build())
                            .collect(Collectors.toList());

                    return CombinationInfo.builder()
                            .id(combination.getId())
                            .title(combination.getName())
                            .description(combination.getDescription())
                            .category(combination.getCategory())
                            .isFeatured(true)
                            .aiServices(aiServiceInfos)
                            .build();
                })
                .collect(Collectors.toList());

        return AiCombinationListResponse.builder()
                .combinations(combinationInfos)
                .totalCount(combinationInfos.size())
                .build();
    }
}