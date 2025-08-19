package com.aimine.aimine.aicombination.domain;

import com.aimine.aimine.aiservice.domain.AiService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ai_combination_services",
        uniqueConstraints = @UniqueConstraint(columnNames = {"combination_id", "ai_service_id"}))
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCombinationService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "combination_id", nullable = false)
    private AiCombination combination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_service_id", nullable = false)
    private AiService aiService;
}