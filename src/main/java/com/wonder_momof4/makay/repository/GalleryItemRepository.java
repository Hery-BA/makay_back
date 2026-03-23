package com.wonder_momof4.makay.repository;

import com.wonder_momof4.makay.model.GalleryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GalleryItemRepository extends JpaRepository<GalleryItem, UUID> {
    List<GalleryItem> findAllByOrderBySortOrderAsc();
    List<GalleryItem> findByCategoryOrderBySortOrderAsc(String category);
}
