package com.aimine.features.relations;

import jakarta.persistence.*;

@Entity
@Table(name = "ai_service_keywords")
public class AiServiceKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long aiServiceId;
    private Long keywordTagId;

    public Long getId() {
        return id;
    }

    public Long getAiServiceId() {
        return aiServiceId;
    }

    public void setAiServiceId(Long a) {
        this.aiServiceId = a;
    }

    public Long getKeywordTagId() {
        return keywordTagId;
    }

    public void setKeywordTagId(Long k) {
        this.keywordTagId = k;
    }
}