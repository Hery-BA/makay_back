package com.wonder_momof4.makay.repository;

import com.wonder_momof4.makay.model.DestinationImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface DestinationImageRepository extends JpaRepository<DestinationImage, UUID> {
    @Modifying
    @Transactional
    @Query("DELETE FROM DestinationImage i WHERE i.destination.id = :destinationId")
    void deleteByDestinationId(@Param("destinationId") UUID destinationId);
}
