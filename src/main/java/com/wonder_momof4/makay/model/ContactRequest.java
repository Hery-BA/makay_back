package com.wonder_momof4.makay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contact_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "full_name", nullable = false) private String fullName;
    @Column(nullable = false) private String email;
    private String phone;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "expedition_id")
    @JsonIgnore
    private Expedition expedition;
    private String subject;
    @Column(nullable = false, columnDefinition = "TEXT") private String message;
    @Column(name = "is_read") @Builder.Default private Boolean isRead = false;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}