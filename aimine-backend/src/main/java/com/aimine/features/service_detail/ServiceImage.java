package com.aimine.features.service_detail;

import com.aimine.core.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "service_images")
public class ServiceImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imageType;
    private String imagePath;

    public Long getId() {
        return id;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String t) {
        this.imageType = t;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String p) {
        this.imagePath = p;
    }
}
