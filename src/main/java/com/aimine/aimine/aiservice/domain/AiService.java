package com.aimine.aimine.aiservice.domain;

import com.aimine.aimine.category.domain.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ai_services")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_path", columnDefinition = "TEXT")
    private String imagePath;

    @Column(columnDefinition = "TEXT")
    private String tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "official_url", columnDefinition = "TEXT")
    private String officialUrl;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_type", length = 20, nullable = false)
    @Builder.Default
    private PricingType pricingType = PricingType.UNKNOWN;

    @Column(name = "pricing_image_path", length = 500)
    private String pricingImagePath;

    @Column(name = "average_rating", precision = 3, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "recommendation_score", precision = 3, scale = 2)
    private BigDecimal recommendationScore;

    @Column(name = "service_image_path", columnDefinition = "TEXT")
    private String serviceImagePath;

    @Column(name = "search_logo_path", columnDefinition = "TEXT")
    private String searchLogoPath;

    @Column(name = "recommendation_rank")
    private Integer recommendationRank;

    @Column(name = "total_reviews")
    private Integer totalReviews;

    public enum PricingType {
        FREE,       // 무료
        FREEMIUM,   // 부분 유료
        PAID,       // 유료
        UNKNOWN     // 알 수 없음
    }

    // 비즈니스 메소드
    public void updateRating(BigDecimal averageRating, Integer totalReviews) {
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    public void updateRecommendation(BigDecimal score, Integer rank) {
        this.recommendationScore = score;
        this.recommendationRank = rank;
    }
}