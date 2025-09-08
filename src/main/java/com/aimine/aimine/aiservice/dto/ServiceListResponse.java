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
        private String tag;   // 기존 호환성 유지 (첫 번째 태그)
        private String tags;  // 새로 추가 - DB의 tags 컬럼 전체 내용
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

    // from 메서드 수정
    public static ServiceListResponse from(List<AiService> aiServices, List<List<String>> keywordsList) {
        String baseUrl = "https://aimine-api-production.up.railway.app"; // API 서버 주소 (환경에 맞게 수정)

        List<ServiceData> serviceDataList = aiServices.stream()
                .map(service -> {
                    int index = aiServices.indexOf(service);
                    List<String> keywords = keywordsList.size() > index ? keywordsList.get(index) : List.of();

                    return ServiceData.builder()
                            .id(service.getId())
                            .serviceName(service.getName())
                            .description(service.getDescription() != null ? service.getDescription() : "AI 서비스 설명")
                            .websiteUrl(service.getOfficialUrl())
                            .logoUrl(service.getImagePath() != null ? baseUrl + service.getImagePath() : null)
                            .launchDate(service.getReleaseDate())
                            .category(CategoryInfo.builder()
                                    .id(service.getCategory().getId())
                                    .name(service.getCategory().getDisplayName())
                                    .build())
                            .tag(service.getTags() != null && !service.getTags().trim().isEmpty() ?
                                    service.getTags().split(",")[0].trim() : "AI 서비스")
                            .tags(service.getTags())
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