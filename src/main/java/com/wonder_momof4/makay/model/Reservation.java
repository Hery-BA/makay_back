package com.wonder_momof4.makay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "reservations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expedition_id")
    @JsonIgnore
    private Expedition expedition;

    @Column(name = "full_name", nullable = false) private String fullName;
    @Column(nullable = false) private String email;
    private String phone;
    private String nationality;
    @Column(nullable = false) @Builder.Default private Integer participants = 1;
    @Column(columnDefinition = "TEXT") private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default private Status status = Status.PENDING;

    @Column(name = "whatsapp_sent") @Builder.Default private Boolean whatsappSent = false;
    @Column(name = "admin_notes", columnDefinition = "TEXT") private String adminNotes;

    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate  protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum Status { PENDING, CONFIRMED, CANCELLED, COMPLETED }
}