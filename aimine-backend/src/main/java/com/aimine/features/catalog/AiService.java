package com.aimine.features.catalog;

import com.aimine.core.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "ai_services")
public class AiService extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String serviceName;
    private String shortDescription;

    public Long getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String s) {
        this.serviceName = s;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String s) {
        this.shortDescription = s;
    }
}
