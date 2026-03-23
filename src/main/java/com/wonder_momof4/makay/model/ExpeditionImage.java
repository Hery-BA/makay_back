package com.wonder_momof4.makay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity @Table(name = "expedition_images")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExpeditionImage {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expedition_id", nullable = false)
    @JsonIgnore
    private Expedition expedition;
    @Column(name = "image_url", nullable = false) private String imageUrl;
    @Column(name = "public_id") private String publicId;
    @Column(name = "caption_fr") private String captionFr;
    @Column(name = "caption_en") private String captionEn;
    @Column(name = "sort_order") @Builder.Default private Integer sortOrder = 0;
}