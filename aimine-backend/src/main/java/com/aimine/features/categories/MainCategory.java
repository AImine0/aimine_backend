package com.aimine.features.categories;

import jakarta.persistence.*;

@Entity
@Table(name = "main_categories")
public class MainCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    private String displayName;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String d) {
        this.displayName = d;
    }
}
