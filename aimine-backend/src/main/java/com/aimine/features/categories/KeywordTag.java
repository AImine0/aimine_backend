package com.aimine.features.categories;

import jakarta.persistence.*;

@Entity
@Table(name = "keyword_tags")
public class KeywordTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String displayName;
    @ManyToOne
    @JoinColumn(name = "main_category_id")
    private MainCategory mainCategory;

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

    public MainCategory getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(MainCategory m) {
        this.mainCategory = m;
    }
}
