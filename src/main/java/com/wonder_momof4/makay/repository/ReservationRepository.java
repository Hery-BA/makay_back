package com.wonder_momof4.makay.repository;

import com.wonder_momof4.makay.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findAllByOrderByCreatedAtDesc();
    List<Reservation> findByStatus(Reservation.Status status);
    List<Reservation> findByExpeditionId(UUID expeditionId);
    long countByStatus(Reservation.Status status);
}
