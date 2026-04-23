package com.wonder_momof4.makay.controller;

import com.wonder_momof4.makay.model.*;
import com.wonder_momof4.makay.repository.*;
import com.wonder_momof4.makay.service.StorageService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DestinationController {

    private final DestinationRepository destinationRepository;
    private final DestinationImageRepository imageRepository;
    private final DestinationWaypointRepository waypointRepository;
    private final StorageService storageService;

    // ─── PUBLIC ──────────────────────────────────────────────────────────────

    @GetMapping("/api/public/destinations")
    public List<Destination> getPublished() {
        return destinationRepository.findByIsPublishedTrueOrderBySortOrderAsc();
    }

    @GetMapping("/api/public/destinations/{slug}")
    public ResponseEntity<Destination> getBySlug(@PathVariable String slug) {
        return destinationRepository.findBySlug(slug)
                .filter(Destination::getIsPublished)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/public/destinations/{slug}/waypoints")
    public ResponseEntity<List<DestinationWaypoint>> getWaypoints(@PathVariable String slug) {
        return destinationRepository.findBySlug(slug)
                .map(d -> ResponseEntity.ok(
                        waypointRepository.findByDestinationIdOrderBySortOrderAsc(d.getId())
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    // ─── ADMIN ───────────────────────────────────────────────────────────────

    @GetMapping("/api/admin/destinations")
    public List<Destination> getAll() {
        return destinationRepository.findAllByOrderBySortOrderAsc();
    }

    @PostMapping("/api/admin/destinations")
    @Transactional
    public ResponseEntity<Destination> create(@RequestBody DestinationRequest req) {
        var dest = Destination.builder()
                .slug(req.slug())
                .titleFr(req.titleFr()).titleEn(req.titleEn())
                .subtitleFr(req.subtitleFr()).subtitleEn(req.subtitleEn())
                .descriptionFr(req.descriptionFr()).descriptionEn(req.descriptionEn())
                .region(req.region())
                .latitude(req.latitude()).longitude(req.longitude())
                .zoomLevel(req.zoomLevel() != null ? req.zoomLevel() : 10)
                .sortOrder(req.sortOrder() != null ? req.sortOrder() : 0)
                .isPublished(false)
                .build();
        return ResponseEntity.ok(destinationRepository.save(dest));
    }

    @PutMapping("/api/admin/destinations/{id}")
    @Transactional
    public ResponseEntity<Destination> update(@PathVariable UUID id,
                                              @RequestBody DestinationRequest req) {
        return destinationRepository.findById(id).map(d -> {
            d.setSlug(req.slug());
            d.setTitleFr(req.titleFr()); d.setTitleEn(req.titleEn());
            d.setSubtitleFr(req.subtitleFr()); d.setSubtitleEn(req.subtitleEn());
            d.setDescriptionFr(req.descriptionFr()); d.setDescriptionEn(req.descriptionEn());
            d.setRegion(req.region());
            d.setLatitude(req.latitude()); d.setLongitude(req.longitude());
            if (req.zoomLevel() != null) d.setZoomLevel(req.zoomLevel());
            if (req.sortOrder() != null) d.setSortOrder(req.sortOrder());
            return ResponseEntity.ok(destinationRepository.save(d));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/api/admin/destinations/{id}/publish")
    public ResponseEntity<Destination> togglePublish(@PathVariable UUID id,
                                                     @RequestParam boolean published) {
        return destinationRepository.findById(id).map(d -> {
            d.setIsPublished(published);
            return ResponseEntity.ok(destinationRepository.save(d));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/admin/destinations/{id}/cover")
    public ResponseEntity<?> uploadCover(@PathVariable UUID id,
                                         @RequestParam("file") MultipartFile file) {
        return destinationRepository.findById(id).map(d -> {
            try {
                var result = storageService.upload(file, "destinations");
                d.setCoverImageUrl(result.get("url"));
                destinationRepository.save(d);
                return ResponseEntity.ok(result);
            } catch (Exception ex) {
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", ex.getMessage()));
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/admin/destinations/{id}/images")
    public ResponseEntity<?> uploadImage(@PathVariable UUID id,
                                         @RequestParam("file") MultipartFile file) {
        return destinationRepository.findById(id).map(d -> {
            try {
                var result = storageService.upload(file, "destinations");
                var image = DestinationImage.builder()
                        .destination(d)
                        .imageUrl(result.get("url"))
                        .sortOrder(d.getImages().size())
                        .build();
                return ResponseEntity.ok(imageRepository.save(image));
            } catch (Exception ex) {
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", ex.getMessage()));
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/admin/destinations/{id}/waypoints")
    public ResponseEntity<DestinationWaypoint> addWaypoint(@PathVariable UUID id,
                                                           @RequestBody WaypointRequest req) {
        return destinationRepository.findById(id).map(d -> {
            var wp = DestinationWaypoint.builder()
                    .destination(d)
                    .name(req.name()).description(req.description())
                    .latitude(req.latitude()).longitude(req.longitude())
                    .type(req.type()).sortOrder(req.sortOrder() != null ? req.sortOrder() : 0)
                    .build();
            return ResponseEntity.ok(waypointRepository.save(wp));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/admin/destinations/{id}/waypoints/clear")
    @Transactional
    public ResponseEntity<Void> clearWaypoints(@PathVariable UUID id) {
        waypointRepository.deleteByDestinationId(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/admin/destinations/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!destinationRepository.existsById(id)) return ResponseEntity.notFound().build();
        destinationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    record DestinationRequest(
            String slug, String titleFr, String titleEn,
            String subtitleFr, String subtitleEn,
            String descriptionFr, String descriptionEn,
            String region, BigDecimal latitude, BigDecimal longitude,
            Integer zoomLevel, Integer sortOrder
    ) {}

    record WaypointRequest(
            String name, String description,
            BigDecimal latitude, BigDecimal longitude,
            String type, Integer sortOrder
    ) {}
}