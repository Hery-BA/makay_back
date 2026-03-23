package com.wonder_momof4.makay.repository;

import com.wonder_momof4.makay.model.ExpeditionWaypoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpeditionWaypointRepository extends JpaRepository<ExpeditionWaypoint, UUID> {
    List<ExpeditionWaypoint> findByExpeditionIdOrderBySortOrderAsc(UUID expeditionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ExpeditionWaypoint w WHERE w.expedition.id = :expeditionId")
    void deleteByExpeditionId(@Param("expeditionId") UUID expeditionId);
}