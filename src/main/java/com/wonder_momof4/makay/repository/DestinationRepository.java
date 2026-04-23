package com.wonder_momof4.makay.repository;

import com.wonder_momof4.makay.model.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, UUID> {
    List<Destination> findByIsPublishedTrueOrderBySortOrderAsc();
    Optional<Destination> findBySlug(String slug);
    List<Destination> findAllByOrderBySortOrderAsc();
}