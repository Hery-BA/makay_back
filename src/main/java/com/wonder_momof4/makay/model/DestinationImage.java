package com.wonder_momof4.makay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "destination_images")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DestinationImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id")
    @JsonIgnore
    private Destination destination;

    private String imageUrl;
    private String captionFr;
    private String captionEn;

    @Builder.Default
    private Integer sortOrder = 0;
}