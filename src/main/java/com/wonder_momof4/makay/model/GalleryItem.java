package com.wonder_momof4.makay.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "gallery_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "image_url", nullable = false) private String imageUrl;
    @Column(name = "public_id") private String publicId;
    private String category;
    @Column(name = "caption_fr") private String captionFr;
    @Column(name = "caption_en") private String captionEn;
    @Column(name = "sort_order") @Builder.Default private Integer sortOrder = 0;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}
