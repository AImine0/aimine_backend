package com.aimine.aimine.aicombination.dto;

import com.aimine.aimine.aicombination.domain.AiCombination;
import com.aimine.aimine.aiservice.domain.AiService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        // ToolCard 표시용 필수 필드들만
        private Long id;                // 상세 페이지 링크용
        private String name;            // 제목
        private String description;     // 설명
        private String logoUrl;         // 로고 (또는 이미지 매핑용 이름)
        private String categoryName;    // 카테고리 뱃지용
        private String tags;            // 태그 뱃지용 (콤마 구분 문자열)
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
                                    .logoUrl("https://logo-url.com/" +
                                            service.getName().toLowerCase() + ".png")
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