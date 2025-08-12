package com.aimine.features.service_detail;

import com.aimine.core.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "external_links")
public class ExternalLink extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String linkType;
    private String url;

    public Long getId() {
        return id;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String l) {
        this.linkType = l;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String u) {
        this.url = u;
    }
}
