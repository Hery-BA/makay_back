package com.wonder_momof4.makay.controller;

import com.wonder_momof4.makay.model.Expedition;
import com.wonder_momof4.makay.model.ExpeditionImage;
import com.wonder_momof4.makay.model.ExpeditionWaypoint;
import com.wonder_momof4.makay.model.ItineraryDay;
import com.wonder_momof4.makay.repository.ExpeditionImageRepository;
import com.wonder_momof4.makay.repository.ExpeditionRepository;
import com.wonder_momof4.makay.repository.ExpeditionWaypointRepository;
import com.wonder_momof4.makay.repository.ItineraryDayRepository;
import com.wonder_momof4.makay.service.StorageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ExpeditionController {

    private final ExpeditionRepository expeditionRepository;
    private final ItineraryDayRepository itineraryDayRepository;
    private final StorageService storageService;
    private final ExpeditionWaypointRepository waypointRepository;
    private final ExpeditionImageRepository imageRepository;
    // ─── PUBLIC ──────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    @GetMapping("/api/public/expeditions")
    public List<Expedition> getPublished() {
        return expeditionRepository.findByIsPublishedTrueOrderByStartDateAsc();
    }
    @Transactional(readOnly = true)
    @GetMapping("/api/public/expeditions/{slug}")
    public ResponseEntity<Expedition> getBySlug(@PathVariable String slug) {
        return expeditionRepository.findBySlug(slug)
                .filter(Expedition::getIsPublished)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─── ADMIN ───────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    @GetMapping("/api/admin/expeditions")
    public List<Expedition> getAll() {
        return expeditionRepository.findAllByOrderByCreatedAtDesc();
    }
    @Transactional(readOnly = true)
    @GetMapping("/api/admin/expeditions/{id}")
    public ResponseEntity<Expedition> getById(@PathVariable UUID id) {
        return expeditionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/admin/expeditions")
    public ResponseEntity<Expedition> create(@Valid @RequestBody ExpeditionRequest req) {
        var expedition = Expedition.builder()
                .slug(req.slug())
                .titleFr(req.titleFr()).titleEn(req.titleEn())
                .descriptionFr(req.descriptionFr()).descriptionEn(req.descriptionEn())
                .durationDays(req.durationDays())
                .difficulty(Expedition.Difficulty.valueOf(req.difficulty()))
                .price(req.price())
                .priceOnRequest(req.priceOnRequest() != null && req.priceOnRequest())
                .startDate(req.startDate()).endDate(req.endDate())
                .spotsTotal(req.spotsTotal() != null ? req.spotsTotal() : 8)
                .spotsRemaining(req.spotsTotal() != null ? req.spotsTotal() : 8)
                .includedFr(req.includedFr()).includedEn(req.includedEn())
                .notIncludedFr(req.notIncludedFr()).notIncludedEn(req.notIncludedEn())
                .whatsappMessage(req.whatsappMessage())
                .isPublished(false)
                .build();

        var saved = expeditionRepository.save(expedition);

        // Sauvegarder les jours d'itinéraire
        if (req.itineraryDays() != null) {
            for (var dayReq : req.itineraryDays()) {
                var day = ItineraryDay.builder()
                        .expedition(saved)
                        .dayNumber(dayReq.dayNumber())
                        .titleFr(dayReq.titleFr())
                        .titleEn(dayReq.titleEn())
                        .descriptionFr(dayReq.descriptionFr())
                        .descriptionEn(dayReq.descriptionEn())
                        .build();
                itineraryDayRepository.save(day);
            }
        }

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/api/admin/expeditions/{id}")
    public ResponseEntity<Expedition> update(@PathVariable UUID id,
                                             @Valid @RequestBody ExpeditionRequest req) {
        return expeditionRepository.findById(id).map(e -> {
            e.setSlug(req.slug());
            e.setTitleFr(req.titleFr()); e.setTitleEn(req.titleEn());
            e.setDescriptionFr(req.descriptionFr()); e.setDescriptionEn(req.descriptionEn());
            e.setDurationDays(req.durationDays());
            e.setDifficulty(Expedition.Difficulty.valueOf(req.difficulty()));
            e.setPrice(req.price());
            e.setPriceOnRequest(req.priceOnRequest() != null && req.priceOnRequest());
            e.setStartDate(req.startDate()); e.setEndDate(req.endDate());
            if (req.spotsTotal() != null) e.setSpotsTotal(req.spotsTotal());
            e.setIncludedFr(req.includedFr()); e.setIncludedEn(req.includedEn());
            e.setNotIncludedFr(req.notIncludedFr()); e.setNotIncludedEn(req.notIncludedEn());
            e.setWhatsappMessage(req.whatsappMessage());

            var saved = expeditionRepository.save(e);

            // Remplacer les jours d'itinéraire
            if (req.itineraryDays() != null) {
                itineraryDayRepository.deleteByExpeditionId(saved.getId());
                for (var dayReq : req.itineraryDays()) {
                    var day = ItineraryDay.builder()
                            .expedition(saved)
                            .dayNumber(dayReq.dayNumber())
                            .titleFr(dayReq.titleFr())
                            .titleEn(dayReq.titleEn())
                            .descriptionFr(dayReq.descriptionFr())
                            .descriptionEn(dayReq.descriptionEn())
                            .build();
                    itineraryDayRepository.save(day);
                }
            }

            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/api/admin/expeditions/{id}/publish")
    public ResponseEntity<Expedition> togglePublish(@PathVariable UUID id,
                                                    @RequestParam boolean published) {
        return expeditionRepository.findById(id).map(e -> {
            e.setIsPublished(published);
            return ResponseEntity.ok(expeditionRepository.save(e));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/admin/expeditions/{id}/cover")
    public ResponseEntity<?> uploadCover(@PathVariable UUID id,
                                         @RequestParam("file") MultipartFile file) {
        return expeditionRepository.findById(id).map(e -> {
            try {
                var result = storageService.upload(file, "expeditions");
                e.setCoverImageUrl(result.get("url"));
                expeditionRepository.save(e);
                return ResponseEntity.ok(result);
            } catch (Exception ex) {
                return ResponseEntity.internalServerError().body("Erreur upload: " + ex.getMessage());
            }
        }).orElse(ResponseEntity.notFound().build());
    }
    @Transactional
    @DeleteMapping("/api/admin/expeditions/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!expeditionRepository.existsById(id)) return ResponseEntity.notFound().build();
        expeditionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ─── RECORDS ─────────────────────────────────────────────────────────────

    record ExpeditionRequest(
            @NotBlank String slug,
            @NotBlank String titleFr, String titleEn,
            String descriptionFr, String descriptionEn,
            Integer durationDays,
            @NotBlank String difficulty,
            BigDecimal price, Boolean priceOnRequest,
            LocalDate startDate, LocalDate endDate,
            Integer spotsTotal,
            String includedFr, String includedEn,
            String notIncludedFr, String notIncludedEn,
            String whatsappMessage,
            List<ItineraryDayRequest> itineraryDays  // ← virgule ajoutée avant
    ) {}

    record ItineraryDayRequest(
            Integer dayNumber,
            String titleFr, String titleEn,
            String descriptionFr, String descriptionEn
    ) {}

    // ─── PUBLIC ───────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    @GetMapping("/api/public/expeditions/{slug}/waypoints")
    public ResponseEntity<List<ExpeditionWaypoint>> getWaypointsPublic(@PathVariable String slug) {
        return expeditionRepository.findBySlug(slug)
                .map(exp -> ResponseEntity.ok(
                        waypointRepository.findByExpeditionIdOrderBySortOrderAsc(exp.getId())
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    // ─── ADMIN ───────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    @GetMapping("/api/admin/expeditions/{id}/waypoints")
    public List<ExpeditionWaypoint> getWaypoints(@PathVariable UUID id) {
        return waypointRepository.findByExpeditionIdOrderBySortOrderAsc(id);
    }

    @PostMapping("/api/admin/expeditions/{id}/waypoints")
    public ResponseEntity<ExpeditionWaypoint> addWaypoint(
            @PathVariable UUID id,
            @RequestBody WaypointRequest req) {
        return expeditionRepository.findById(id).map(exp -> {
            var waypoint = ExpeditionWaypoint.builder()
                    .expedition(exp)
                    .name(req.name())
                    .description(req.description())
                    .latitude(req.latitude())
                    .longitude(req.longitude())
                    .type(req.type())
                    .sortOrder(req.sortOrder() != null ? req.sortOrder() : 0)
                    .build();
            return ResponseEntity.ok(waypointRepository.save(waypoint));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/api/admin/expeditions/{id}/waypoints/{waypointId}")
    public ResponseEntity<ExpeditionWaypoint> updateWaypoint(
            @PathVariable UUID id,
            @PathVariable UUID waypointId,
            @RequestBody WaypointRequest req) {
        return waypointRepository.findById(waypointId).map(w -> {
            w.setName(req.name());
            w.setDescription(req.description());
            w.setLatitude(req.latitude());
            w.setLongitude(req.longitude());
            w.setType(req.type());
            if (req.sortOrder() != null) w.setSortOrder(req.sortOrder());
            return ResponseEntity.ok(waypointRepository.save(w));
        }).orElse(ResponseEntity.notFound().build());
    }
    @Transactional
    @DeleteMapping("/api/admin/expeditions/{id}/waypoints/{waypointId}")
    public ResponseEntity<Void> deleteWaypoint(
            @PathVariable UUID id,
            @PathVariable UUID waypointId) {
        if (!waypointRepository.existsById(waypointId))
            return ResponseEntity.notFound().build();
        waypointRepository.deleteById(waypointId);
        return ResponseEntity.noContent().build();
    }

    record WaypointRequest(
            String name, String description,
            java.math.BigDecimal latitude,
            java.math.BigDecimal longitude,
            String type, Integer sortOrder
    ) {}
    @Transactional
    @DeleteMapping("/api/admin/expeditions/{id}/waypoints/clear")
    public ResponseEntity<Void> clearWaypoints(@PathVariable UUID id) {
        waypointRepository.deleteByExpeditionId(id);
        return ResponseEntity.noContent().build();
    }


//    =============
@PostMapping("/api/admin/expeditions/{id}/images")
public ResponseEntity<?> uploadImage(@PathVariable UUID id,
                                     @RequestParam("file") MultipartFile file) {
    return expeditionRepository.findById(id).map(e -> {
        try {
            var result = storageService.upload(file, "expeditions");
            var image = ExpeditionImage.builder()
                    .expedition(e)
                    .imageUrl(result.get("url"))
                    .publicId(result.get("path"))
                    .sortOrder(e.getImages().size())
                    .build();
            return ResponseEntity.ok(imageRepository.save(image));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", ex.getMessage()));
        }
    }).orElse(ResponseEntity.notFound().build());
}
    @Transactional
    @DeleteMapping("/api/admin/expeditions/{id}/images/{imageId}")
    public ResponseEntity<Object> deleteImage(@PathVariable UUID id,
                                              @PathVariable UUID imageId) {
        return imageRepository.findById(imageId).map(img -> {
            if (img.getPublicId() != null) storageService.delete(img.getPublicId());
            imageRepository.delete(img);
            return ResponseEntity.<Void>noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

}