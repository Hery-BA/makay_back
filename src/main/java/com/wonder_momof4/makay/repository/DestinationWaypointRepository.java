package com.wonder_momof4.makay.repository;

import com.wonder_momof4.makay.model.DestinationWaypoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface DestinationWaypointRepository extends JpaRepository<DestinationWaypoint, UUID> {
    List<DestinationWaypoint> findByDestinationIdOrderBySortOrderAsc(UUID destinationId);

    @Modifying
    @Transactional
    @Query("DELETE FROM DestinationWaypoint w WHERE w.destination.id = :destinationId")
    void deleteByDestinationId(@Param("destinationId") UUID destinationId);
}
