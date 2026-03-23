package com.wonder_momof4.makay.repository;

import com.wonder_momof4.makay.model.ItineraryDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface ItineraryDayRepository extends JpaRepository<ItineraryDay, UUID> {
    @Modifying
    @Transactional
    @Query("DELETE FROM ItineraryDay d WHERE d.expedition.id = :expeditionId")
    void deleteByExpeditionId(@Param("expeditionId") UUID expeditionId);
}