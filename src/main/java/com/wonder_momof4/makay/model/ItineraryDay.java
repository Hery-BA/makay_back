package com.wonder_momof4.makay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "itinerary_days")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ItineraryDay {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expedition_id", nullable = false)
    @JsonIgnore
    private Expedition expedition;
    @Column(name = "day_number", nullable = false) private Integer dayNumber;
    @Column(name = "title_fr") private String titleFr;
    @Column(name = "title_en") private String titleEn;
    @Column(name = "description_fr", columnDefinition = "TEXT") private String descriptionFr;
    @Column(name = "description_en", columnDefinition = "TEXT") private String descriptionEn;
}