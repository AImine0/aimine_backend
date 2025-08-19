package com.aimine.aimine.category.dto;

import com.aimine.aimine.category.domain.Category;
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
public class CategoryListResponse {

    private List<CategoryInfo> categories;
    private Integer totalCount;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long id;
        private String name;
        private String displayName;
        private Long serviceCount;
    }

    // 정적 팩토리 메소드
    public static CategoryListResponse from(List<Category> categories, List<Long> serviceCounts) {
        List<CategoryInfo> categoryInfos = categories.stream()
                .map(category -> {
                    int index = categories.indexOf(category);
                    Long serviceCount = serviceCounts.size() > index ? serviceCounts.get(index) : 0L;

                    return CategoryInfo.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .displayName(category.getDisplayName())
                            .serviceCount(serviceCount)
                            .build();
                })
                .collect(Collectors.toList());

        return CategoryListResponse.builder()
                .categories(categoryInfos)
                .totalCount(categoryInfos.size())
                .build();
    }
}