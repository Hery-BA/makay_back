package com.wonder_momof4.makay.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "expedition_waypoints")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExpeditionWaypoint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expedition_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Expedition expedition;

    private String name;
    private String description;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Builder.Default
    private String type = "POINT_OF_INTEREST";

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    public enum Type {
        START, END, POINT_OF_INTEREST, CAMP
    }
}
