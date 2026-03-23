package com.wonder_momof4.makay.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity @Table(name = "expeditions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Expedition {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "title_fr", nullable = false) private String titleFr;
    @Column(name = "title_en") private String titleEn;
    @Column(name = "description_fr", columnDefinition = "TEXT") private String descriptionFr;
    @Column(name = "description_en", columnDefinition = "TEXT") private String descriptionEn;
    @Column(name = "duration_days", nullable = false) private Integer durationDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    private BigDecimal price;
    @Builder.Default private String currency = "EUR";
    @Column(name = "price_on_request") @Builder.Default private Boolean priceOnRequest = false;
    @Column(name = "start_date") private LocalDate startDate;
    @Column(name = "end_date") private LocalDate endDate;
    @Column(name = "spots_total") @Builder.Default private Integer spotsTotal = 8;
    @Column(name = "spots_remaining") @Builder.Default private Integer spotsRemaining = 8;
    @Column(name = "included_fr", columnDefinition = "TEXT") private String includedFr;
    @Column(name = "included_en", columnDefinition = "TEXT") private String includedEn;
    @Column(name = "not_included_fr", columnDefinition = "TEXT") private String notIncludedFr;
    @Column(name = "not_included_en", columnDefinition = "TEXT") private String notIncludedEn;
    @Column(name = "cover_image_url") private String coverImageUrl;
    @Column(name = "whatsapp_message", columnDefinition = "TEXT") private String whatsappMessage;
    @Column(name = "is_published") @Builder.Default private Boolean isPublished = false;

    @OneToMany(mappedBy = "expedition", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dayNumber ASC")
    @Builder.Default
    private List<ItineraryDay> itineraryDays = new ArrayList<>();

    @OneToMany(mappedBy = "expedition", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<ExpeditionImage> images = new ArrayList<>();
    @OneToMany(mappedBy = "expedition", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<ExpeditionWaypoint> waypoints = new ArrayList<>();
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate  protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum Difficulty { DISCOVERY, INTERMEDIATE, EXPEDITION }
}