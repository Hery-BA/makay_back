package com.wonder_momof4.makay.controller;

import com.wonder_momof4.makay.model.ContactRequest;
import com.wonder_momof4.makay.repository.ContactRequestRepository;
import com.wonder_momof4.makay.repository.ExpeditionRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
class ContactController {

    private final ContactRequestRepository contactRepository;
    private final ExpeditionRepository expeditionRepository;

    @PostMapping("/api/public/contact")
    public ResponseEntity<?> submit(@Valid @RequestBody ContactFormRequest req) {
        var contact = ContactRequest.builder()
                .fullName(req.fullName())
                .email(req.email())
                .phone(req.phone())
                .subject(req.subject())
                .message(req.message())
                .build();
        if (req.expeditionId() != null) {
            expeditionRepository.findById(req.expeditionId())
                    .ifPresent(contact::setExpedition);
        }
        contactRepository.save(contact);
        return ResponseEntity.ok(Map.of("message", "Votre message a été envoyé avec succès."));
    }

    @GetMapping("/api/admin/contacts")
    public List<ContactRequest> getAll() {
        return contactRepository.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/api/admin/contacts/unread")
    public List<ContactRequest> getUnread() {
        return contactRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    @GetMapping("/api/admin/contacts/unread-count")
    public ResponseEntity<?> unreadCount() {
        return ResponseEntity.ok(Map.of("count", contactRepository.countByIsReadFalse()));
    }

    @PatchMapping("/api/admin/contacts/{id}/read")
    public ResponseEntity<ContactRequest> markRead(@PathVariable UUID id) {
        return contactRepository.findById(id).map(c -> {
            c.setIsRead(true);
            return ResponseEntity.ok(contactRepository.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/admin/contacts/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!contactRepository.existsById(id)) return ResponseEntity.notFound().build();
        contactRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    record ContactFormRequest(
            @NotBlank String fullName,
            @Email @NotBlank String email,
            String phone, String subject,
            @NotBlank String message,
            UUID expeditionId
    ) {}
}