package com.wonder_momof4.makay.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "blog_posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BlogPost {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, unique = true) private String slug;
    @Column(name = "title_fr", nullable = false) private String titleFr;
    @Column(name = "title_en") private String titleEn;
    @Column(name = "excerpt_fr", columnDefinition = "TEXT") private String excerptFr;
    @Column(name = "excerpt_en", columnDefinition = "TEXT") private String excerptEn;
    @Column(name = "content_fr", columnDefinition = "TEXT") private String contentFr;
    @Column(name = "content_en", columnDefinition = "TEXT") private String contentEn;
    @Column(name = "cover_image") private String coverImage;
    @Column(name = "public_id") private String publicId;
    @Builder.Default private String author = "Wonder Mum of 4";
    @Column(name = "is_published") @Builder.Default private Boolean isPublished = false;
    @Column(name = "published_at") private LocalDateTime publishedAt;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate  protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}