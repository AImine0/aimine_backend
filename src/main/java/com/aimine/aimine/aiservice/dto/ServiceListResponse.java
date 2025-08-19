package com.aimine.aimine.aiservice.dto;

import com.aimine.aimine.aiservice.domain.AiService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceListResponse {

    private boolean success;
    private List<ServiceData> data;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceData {
        private Long id;
        private String serviceName;
        private String description;
        private String websiteUrl;
        private String logoUrl;
        private LocalDate launchDate;
        private CategoryInfo category;
        private String tag;
        private String pricingType;
        private BigDecimal overallRating;
        private List<String> keywords;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long id;
        private String name;
    }

    // 정적 팩토리 메소드
    public static ServiceListResponse from(List<AiService> aiServices, List<List<String>> keywordsList) {
        List<ServiceData> serviceDataList = aiServices.stream()
                .map(service -> {
                    int index = aiServices.indexOf(service);
                    List<String> keywords = keywordsList.size() > index ? keywordsList.get(index) : List.of();

                    return ServiceData.builder()
                            .id(service.getId())
                            .serviceName(service.getName())
                            .description("AI 서비스 설명") // 임시 설명
                            .websiteUrl(service.getOfficialUrl())
                            .logoUrl("https://logo-url.com/" + service.getName().toLowerCase() + ".png") // 임시 로고
                            .launchDate(service.getReleaseDate())
                            .category(CategoryInfo.builder()
                                    .id(service.getCategory().getId())
                                    .name(service.getCategory().getDisplayName())
                                    .build())
                            .tag(service.getTags() != null && service.getTags().length > 0 ?
                                    service.getTags()[0] : "AI 서비스") // 첫 번째 태그 또는 기본값
                            .pricingType(service.getPricingType().name())
                            .overallRating(service.getAverageRating())
                            .keywords(keywords)
                            .build();
                })
                .collect(Collectors.toList());

        return ServiceListResponse.builder()
                .success(true)
                .data(serviceDataList)
                .build();
    }
}