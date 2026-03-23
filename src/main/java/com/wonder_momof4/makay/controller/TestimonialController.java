package com.wonder_momof4.makay.controller;

import com.wonder_momof4.makay.model.Testimonial;
import com.wonder_momof4.makay.repository.ExpeditionRepository;
import com.wonder_momof4.makay.repository.TestimonialRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TestimonialController {

    private final TestimonialRepository testimonialRepository;
    private final ExpeditionRepository expeditionRepository;

    @GetMapping("/api/public/testimonials")
    public List<Testimonial> getApproved() {
        return testimonialRepository.findByIsApprovedTrueOrderByCreatedAtDesc();
    }

    @PostMapping("/api/public/testimonials")
    public ResponseEntity<Testimonial> submit(@Valid @RequestBody TestimonialRequest req) {
        var testimonial = Testimonial.builder()
                .authorName(req.authorName())
                .country(req.country())
                .rating(req.rating() != null ? req.rating() : 5)
                .contentFr(req.contentFr())
                .contentEn(req.contentEn())
                .isApproved(false)
                .build();
        if (req.expeditionId() != null) {
            expeditionRepository.findById(req.expeditionId())
                    .ifPresent(testimonial::setExpedition);
        }
        return ResponseEntity.ok(testimonialRepository.save(testimonial));
    }

    @GetMapping("/api/admin/testimonials")
    public List<Testimonial> getAll() {
        return testimonialRepository.findAllByOrderByCreatedAtDesc();
    }

    @PatchMapping("/api/admin/testimonials/{id}/approve")
    public ResponseEntity<Testimonial> approve(@PathVariable UUID id,
                                               @RequestParam boolean approved) {
        return testimonialRepository.findById(id).map(t -> {
            t.setIsApproved(approved);
            return ResponseEntity.ok(testimonialRepository.save(t));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/admin/testimonials/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!testimonialRepository.existsById(id)) return ResponseEntity.notFound().build();
        testimonialRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    record TestimonialRequest(
            @NotBlank String authorName, String country,
            Integer rating, UUID expeditionId,
            String contentFr, String contentEn
    ) {}
}
