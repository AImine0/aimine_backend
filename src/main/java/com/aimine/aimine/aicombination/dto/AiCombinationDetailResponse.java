package com.aimine.aimine.aicombination.dto;

import com.aimine.aimine.aicombination.domain.AiCombination;
import com.aimine.aimine.aiservice.domain.AiService;
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
public class AiCombinationDetailResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private Boolean isFeatured;
    private List<AiServiceDetail> aiServices;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiServiceDetail {
        private Long id;
        private String name;
        private String logoUrl;
        private String purpose;
        private String tag;
    }

    // 정적 팩토리 메소드
    public static AiCombinationDetailResponse from(AiCombination combination, List<AiService> aiServices) {
        List<AiServiceDetail> aiServiceDetails = aiServices.stream()
                .map(service -> AiServiceDetail.builder()
                        .id(service.getId())
                        .name(service.getName())
                        .logoUrl("https://example.com/" + service.getName().toLowerCase() + "-logo.png")
                        .purpose(getPurposeByService(service.getName())) // 서비스별 목적 설정
                        .tag(getTagByService(service)) // 서비스별 태그 설정
                        .build())
                .collect(Collectors.toList());

        return AiCombinationDetailResponse.builder()
                .id(combination.getId())
                .title(combination.getName())
                .description(combination.getDescription())
                .category(combination.getCategory())
                .isFeatured(true) // 임시로 모두 featured로 설정
                .aiServices(aiServiceDetails)
                .build();
    }

    // 서비스별 목적 설정 (임시)
    private static String getPurposeByService(String serviceName) {
        if (serviceName.toLowerCase().contains("chatgpt")) {
            return "사용자 페르소나 분석 및 아이디어 브레인스토밍";
        } else if (serviceName.toLowerCase().contains("midjourney")) {
            return "비주얼 컨셉 및 목업 이미지 생성";
        } else if (serviceName.toLowerCase().contains("figma")) {
            return "와이어프레임 및 프로토타입 제작";
        } else {
            return "업무 효율성 향상 및 창작 지원";
        }
    }

    // 서비스별 태그 설정 (임시)
    private static String getTagByService(AiService service) {
        if (service.getTags() != null && service.getTags().length > 0) {
            return service.getTags()[0];
        }

        String categoryName = service.getCategory().getDisplayName();
        if (categoryName.contains("챗봇")) {
            return "챗봇";
        } else if (categoryName.contains("이미지")) {
            return "이미지";
        } else if (categoryName.contains("디자인")) {
            return "디자인";
        } else {
            return "AI 도구";
        }
    }
}