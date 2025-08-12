package com.aimine.features.reviews;

import com.aimine.core.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long aiServiceId;
    private int rating;
    private String comment;

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

    public int getRating() {
        return rating;
    }

    public void setRating(int r) {
        this.rating = r;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String c) {
        this.comment = c;
    }
}