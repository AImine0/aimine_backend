package com.aimine.features.jobs;

import jakarta.persistence.*;

@Entity
@Table(name = "job_categories")
public class JobCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        this.name = n;
    }
}