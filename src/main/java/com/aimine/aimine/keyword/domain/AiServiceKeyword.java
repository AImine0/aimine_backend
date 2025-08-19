package com.aimine.aimine.keyword.domain;

import com.aimine.aimine.aiservice.domain.AiService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ai_service_keywords",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ai_service_id", "keyword_id"}))
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiServiceKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_service_id", nullable = false)
    private AiService aiService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;
}