package com.aimine.aimine.keyword.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "keywords")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    @Builder.Default
    private KeywordType type = KeywordType.FEATURE;

    public enum KeywordType {
        FEATURE,    // 기능 키워드
        FUNCTION,   // 함수 키워드
        INDUSTRY,   // 산업 키워드
        USE_CASE    // 사용 사례 키워드
    }
}