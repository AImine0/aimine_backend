package com.aimine.aimine.bookmark.domain;

import com.aimine.aimine.aiservice.domain.AiService;
import com.aimine.aimine.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookmarks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ai_service_id"}))
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_service_id", nullable = false)
    private AiService aiService;
}