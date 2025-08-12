package com.aimine.features.bookmarks;

import com.aimine.core.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "bookmarks")
public class Bookmark extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long aiServiceId;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long u) {
        this.userId = u;
    }

    public Long getAiServiceId() {
        return aiServiceId;
    }

    public void setAiServiceId(Long a) {
        this.aiServiceId = a;
    }
}