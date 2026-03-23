package com.wonder_momof4.makay.controller;


import com.wonder_momof4.makay.model.Expedition;
import com.wonder_momof4.makay.model.Reservation;
import com.wonder_momof4.makay.repository.ExpeditionRepository;
import com.wonder_momof4.makay.repository.ReservationRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final ExpeditionRepository expeditionRepository;
    @Value("${whatsapp.phone.admin:}")
    private String adminPhoneNumber;
    // ─── PUBLIC ──────────────────────────────────────────────────────────────

    /**
     * Crée une demande de réservation et retourne le lien WhatsApp
     */
    @PostMapping("/api/public/reservations")
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequest req) {
        var reservation = Reservation.builder()
                .fullName(req.fullName())
                .email(req.email())
                .phone(req.phone())
                .nationality(req.nationality())
                .participants(req.participants() != null ? req.participants() : 1)
                .message(req.message())
                .status(Reservation.Status.PENDING)
                .build();

        // Lier à l'expédition si fournie
        if (req.expeditionId() != null) {
            expeditionRepository.findById(req.expeditionId())
                    .ifPresent(reservation::setExpedition);
        }

        var saved = reservationRepository.save(reservation);

        // Générer le lien WhatsApp
        String whatsappLink = buildWhatsappLink(saved, req);

        return ResponseEntity.ok(Map.of(
                "id",            saved.getId(),
                "status",        saved.getStatus(),
                "whatsappLink",  whatsappLink,
                "message",       "Votre demande a été enregistrée. Cliquez sur le lien WhatsApp pour finaliser."
        ));
    }

    // ─── ADMIN ───────────────────────────────────────────────────────────────

    @GetMapping("/api/admin/reservations")
    public List<Reservation> getAll() {
        return reservationRepository.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/api/admin/reservations/{id}")
    public ResponseEntity<Reservation> getById(@PathVariable UUID id) {
        return reservationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/admin/reservations/by-status/{status}")
    public List<Reservation> getByStatus(@PathVariable String status) {
        return reservationRepository.findByStatus(Reservation.Status.valueOf(status.toUpperCase()));
    }

    @PatchMapping("/api/admin/reservations/{id}/status")
    public ResponseEntity<Reservation> updateStatus(@PathVariable UUID id,
                                                    @RequestParam String status) {
        return reservationRepository.findById(id).map(r -> {
            Reservation.Status oldStatus = r.getStatus();
            Reservation.Status newStatus = Reservation.Status.valueOf(status.toUpperCase());

            // Synchroniser les places restantes
            if (r.getExpedition() != null) {
                Expedition expedition = r.getExpedition();

                // PENDING → CONFIRMED : diminuer les places
                if (oldStatus == Reservation.Status.PENDING
                        && newStatus == Reservation.Status.CONFIRMED) {
                    int participants = r.getParticipants() != null ? r.getParticipants() : 1;
                    int newSpots = Math.max(0, expedition.getSpotsRemaining() - participants);
                    expedition.setSpotsRemaining(newSpots);
                    expeditionRepository.save(expedition);
                }

                // CONFIRMED → CANCELLED : remettre les places
                if (oldStatus == Reservation.Status.CONFIRMED
                        && newStatus == Reservation.Status.CANCELLED) {
                    int participants = r.getParticipants() != null ? r.getParticipants() : 1;
                    int newSpots = Math.min(
                            expedition.getSpotsTotal(),
                            expedition.getSpotsRemaining() + participants
                    );
                    expedition.setSpotsRemaining(newSpots);
                    expeditionRepository.save(expedition);
                }
            }

            r.setStatus(newStatus);
            return ResponseEntity.ok(reservationRepository.save(r));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/api/admin/reservations/{id}/notes")
    public ResponseEntity<Reservation> updateNotes(@PathVariable UUID id,
                                                   @RequestBody Map<String, String> body) {
        return reservationRepository.findById(id).map(r -> {
            r.setAdminNotes(body.get("notes"));
            return ResponseEntity.ok(reservationRepository.save(r));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/admin/reservations/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(Map.of(
                "total",     reservationRepository.count(),
                "pending",   reservationRepository.countByStatus(Reservation.Status.PENDING),
                "confirmed", reservationRepository.countByStatus(Reservation.Status.CONFIRMED),
                "cancelled", reservationRepository.countByStatus(Reservation.Status.CANCELLED),
                "completed", reservationRepository.countByStatus(Reservation.Status.COMPLETED)
        ));
    }

    @DeleteMapping("/api/admin/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!reservationRepository.existsById(id)) return ResponseEntity.notFound().build();
        reservationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ─── HELPER ──────────────────────────────────────────────────────────────

    private String buildWhatsappLink(Reservation saved, ReservationRequest req) {
        // Numéro WhatsApp de la fondatrice (à configurer en variable d'env)
        String phone = System.getenv().getOrDefault("WHATSAPP_NUMBER", adminPhoneNumber);

        StringBuilder msg = new StringBuilder();
        msg.append("Bonjour, je souhaite réserver une expédition Makay Adventures.%0A%0A");
        msg.append("*Nom :* ").append(req.fullName()).append("%0A");
        msg.append("*Email :* ").append(req.email()).append("%0A");
        if (req.phone() != null) msg.append("*Téléphone :* ").append(req.phone()).append("%0A");
        if (req.nationality() != null) msg.append("*Nationalité :* ").append(req.nationality()).append("%0A");
        msg.append("*Participants :* ").append(req.participants()).append("%0A");

        if (saved.getExpedition() != null) {
            msg.append("*Expédition :* ").append(saved.getExpedition().getTitleFr()).append("%0A");
        }

        if (req.message() != null && !req.message().isBlank()) {
            msg.append("%0A*Message :* ").append(req.message());
        }

        msg.append("%0A%0A*Référence :* #").append(saved.getId().toString().substring(0, 8).toUpperCase());

        return "https://wa.me/" + phone + "?text=" + msg;
    }

    record ReservationRequest(
            @NotBlank String fullName,
            @Email @NotBlank String email,
            String phone,
            String nationality,
            Integer participants,
            UUID expeditionId,
            String message
    ) {}
}
