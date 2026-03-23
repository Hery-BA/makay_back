package com.wonder_momof4.makay.controller;


import com.wonder_momof4.makay.model.Reservation;
import com.wonder_momof4.makay.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ExpeditionRepository expeditionRepository;
    private final ReservationRepository reservationRepository;
    private final BlogPostRepository blogRepository;
    private final ContactRequestRepository contactRepository;
    private final TestimonialRepository testimonialRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(Map.of(
                "expeditions", Map.of(
                        "total",     expeditionRepository.count(),
                        "published", expeditionRepository.findByIsPublishedTrueOrderByStartDateAsc().size()
                ),
                "reservations", Map.of(
                        "total",     reservationRepository.count(),
                        "pending",   reservationRepository.countByStatus(Reservation.Status.PENDING),
                        "confirmed", reservationRepository.countByStatus(Reservation.Status.CONFIRMED)
                ),
                "blog", Map.of(
                        "total",     blogRepository.count(),
                        "published", blogRepository.findByIsPublishedTrueOrderByPublishedAtDesc().size()
                ),
                "contacts", Map.of(
                        "total",  contactRepository.count(),
                        "unread", contactRepository.countByIsReadFalse()
                ),
                "testimonials", Map.of(
                        "total",    testimonialRepository.count(),
                        "pending",  testimonialRepository.findAllByOrderByCreatedAtDesc()
                                .stream().filter(t -> !t.getIsApproved()).count()
                )
        ));
    }
}
