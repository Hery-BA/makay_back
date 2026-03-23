package com.wonder_momof4.makay.controller;


import com.wonder_momof4.makay.model.BlogPost;
import com.wonder_momof4.makay.repository.BlogPostRepository;
import com.wonder_momof4.makay.service.StorageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BlogController {

    private final BlogPostRepository blogRepository;
    private final StorageService storageService;

    // ─── PUBLIC ──────────────────────────────────────────────────────────────

    @GetMapping("/api/public/blog")
    public List<BlogPost> getPublished() {
        return blogRepository.findByIsPublishedTrueOrderByPublishedAtDesc();
    }

    @GetMapping("/api/public/blog/{slug}")
    public ResponseEntity<BlogPost> getBySlug(@PathVariable String slug) {
        return blogRepository.findBySlug(slug)
                .filter(BlogPost::getIsPublished)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─── ADMIN ───────────────────────────────────────────────────────────────

    @GetMapping("/api/admin/blog")
    public List<BlogPost> getAll() {
        return blogRepository.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/api/admin/blog/{id}")
    public ResponseEntity<BlogPost> getById(@PathVariable UUID id) {
        return blogRepository.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/admin/blog")
    public ResponseEntity<BlogPost> create(@Valid @RequestBody BlogRequest req) {
        var post = BlogPost.builder()
                .slug(req.slug())
                .titleFr(req.titleFr()).titleEn(req.titleEn())
                .excerptFr(req.excerptFr()).excerptEn(req.excerptEn())
                .contentFr(req.contentFr()).contentEn(req.contentEn())
                .author(req.author() != null ? req.author() : "Wonder Mum of 4")
                .isPublished(false)
                .build();
        return ResponseEntity.ok(blogRepository.save(post));
    }

    @PutMapping("/api/admin/blog/{id}")
    public ResponseEntity<BlogPost> update(@PathVariable UUID id,
                                           @Valid @RequestBody BlogRequest req) {
        return blogRepository.findById(id).map(p -> {
            p.setSlug(req.slug());
            p.setTitleFr(req.titleFr()); p.setTitleEn(req.titleEn());
            p.setExcerptFr(req.excerptFr()); p.setExcerptEn(req.excerptEn());
            p.setContentFr(req.contentFr()); p.setContentEn(req.contentEn());
            if (req.author() != null) p.setAuthor(req.author());
            return ResponseEntity.ok(blogRepository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/api/admin/blog/{id}/publish")
    public ResponseEntity<BlogPost> togglePublish(@PathVariable UUID id,
                                                  @RequestParam boolean published) {
        return blogRepository.findById(id).map(p -> {
            p.setIsPublished(published);
            if (published && p.getPublishedAt() == null) p.setPublishedAt(LocalDateTime.now());
            return ResponseEntity.ok(blogRepository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/admin/blog/{id}/cover")
    public ResponseEntity<?> uploadCover(@PathVariable UUID id,
                                         @RequestParam("file") MultipartFile file) {
        return blogRepository.findById(id).map(p -> {
            try {
                var result = storageService.upload(file, "blog");
                if (p.getPublicId() != null) storageService.delete(p.getPublicId());
                p.setCoverImage(result.get("url"));
                p.setPublicId(result.get("path"));
                blogRepository.save(p);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Erreur upload: " + e.getMessage());
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/admin/blog/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!blogRepository.existsById(id)) return ResponseEntity.notFound().build();
        blogRepository.findById(id).ifPresent(p -> {
            if (p.getPublicId() != null) storageService.delete(p.getPublicId());
        });
        blogRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    record BlogRequest(
            @NotBlank String slug,
            @NotBlank String titleFr, String titleEn,
            String excerptFr, String excerptEn,
            String contentFr, String contentEn,
            String author
    ) {}
}