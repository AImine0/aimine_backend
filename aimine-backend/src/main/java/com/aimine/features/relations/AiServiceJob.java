package com.aimine.features.relations;

import jakarta.persistence.*;

@Entity
@Table(name = "ai_service_jobs")
public class AiServiceJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long aiServiceId;
    private Long jobCategoryId;

    public Long getId() {
        return id;
    }

    public Long getAiServiceId() {
        return aiServiceId;
    }

    public void setAiServiceId(Long a) {
        this.aiServiceId = a;
    }

    public Long getJobCategoryId() {
        return jobCategoryId;
    }

    public void setJobCategoryId(Long j) {
        this.jobCategoryId = j;
    }
}