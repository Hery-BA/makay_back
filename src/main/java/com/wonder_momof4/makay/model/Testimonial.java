package com.wonder_momof4.makay.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity @Table(name = "testimonials")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Testimonial {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "author_name", nullable = false) private String authorName;
    private String country;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "expedition_id") private Expedition expedition;
    @Column(nullable = false) @Builder.Default private Integer rating = 5;
    @Column(name = "content_fr", columnDefinition = "TEXT") private String contentFr;
    @Column(name = "content_en", columnDefinition = "TEXT") private String contentEn;
    @Column(name = "photo_url") private String photoUrl;
    @Column(name = "is_approved") @Builder.Default private Boolean isApproved = false;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}





