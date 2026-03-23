package com.wonder_momof4.makay.repository;

import com.wonder_momof4.makay.model.ExpeditionImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpeditionImageRepository extends JpaRepository<ExpeditionImage, UUID> {
    List<ExpeditionImage> findByExpeditionIdOrderBySortOrderAsc(UUID expeditionId);
}