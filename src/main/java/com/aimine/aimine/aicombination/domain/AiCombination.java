package com.aimine.aimine.aicombination.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ai_combinations")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCombination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String phrase;

    @Column(length = 50)
    private String category;

    // 비즈니스 메소드
    public boolean hasCategory() {
        return category != null && !category.trim().isEmpty();
    }
}