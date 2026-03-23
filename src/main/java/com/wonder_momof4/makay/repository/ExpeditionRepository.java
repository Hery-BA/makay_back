package com.wonder_momof4.makay.repository;


import com.wonder_momof4.makay.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpeditionRepository extends JpaRepository<Expedition, UUID> {
    Optional<Expedition> findBySlug(String slug);
    List<Expedition> findByIsPublishedTrueOrderByStartDateAsc();
    List<Expedition> findAllByOrderByCreatedAtDesc();
}










