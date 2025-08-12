package com.aimine.features.jobs;

import jakarta.persistence.*;

@Entity
@Table(name = "job_situation_images")
public class JobSituationImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String situationName;
    private String imagePath;

    public Long getId() {
        return id;
    }

    public String getSituationName() {
        return situationName;
    }

    public void setSituationName(String s) {
        this.situationName = s;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String p) {
        this.imagePath = p;
    }
}