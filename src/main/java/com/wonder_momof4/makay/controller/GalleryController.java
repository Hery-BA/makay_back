package com.wonder_momof4.makay.controller;

import com.wonder_momof4.makay.model.GalleryItem;
import com.wonder_momof4.makay.repository.GalleryItemRepository;
import com.wonder_momof4.makay.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// ─── GALLERY ──────────────────────────────────────────────────────────────────

@RestController
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryItemRepository galleryRepository;
    private final StorageService storageService;

    @GetMapping("/api/public/gallery")
    public List<GalleryItem> getAll(@RequestParam(required = false) String category) {
        return category != null
                ? galleryRepository.findByCategoryOrderBySortOrderAsc(category.toUpperCase())
                : galleryRepository.findAllByOrderBySortOrderAsc();
    }

    @PostMapping("/api/admin/gallery")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam(required = false) String category,
                                    @RequestParam(required = false) String captionFr,
                                    @RequestParam(required = false) String captionEn) {
        try {
            var result = storageService.upload(file, "gallery");
            var item = GalleryItem.builder()
                    .imageUrl(result.get("url"))
                    .publicId(result.get("path"))
                    .category(category != null ? category.toUpperCase() : "OTHER")
                    .captionFr(captionFr).captionEn(captionEn)
                    .build();
            return ResponseEntity.ok(galleryRepository.save(item));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur upload: " + e.getMessage());
        }
    }

    @PatchMapping("/api/admin/gallery/{id}")
    public ResponseEntity<GalleryItem> update(@PathVariable UUID id,
                                              @RequestBody Map<String, Object> body) {
        return galleryRepository.findById(id).map(item -> {
            if (body.containsKey("category"))  item.setCategory(body.get("category").toString().toUpperCase());
            if (body.containsKey("captionFr")) item.setCaptionFr(body.get("captionFr").toString());
            if (body.containsKey("captionEn")) item.setCaptionEn(body.get("captionEn").toString());
            if (body.containsKey("sortOrder")) item.setSortOrder((Integer) body.get("sortOrder"));
            return ResponseEntity.ok(galleryRepository.save(item));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/admin/gallery/{id}")
    public ResponseEntity<Object> delete(@PathVariable UUID id) {
        return galleryRepository.findById(id).map(item -> {
            if (item.getPublicId() != null) storageService.delete(item.getPublicId());
            galleryRepository.delete(item);
            return ResponseEntity.<Void>noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}