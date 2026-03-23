package com.wonder_momof4.makay.repository;

import com.wonder_momof4.makay.model.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, UUID> {
    List<Testimonial> findByIsApprovedTrueOrderByCreatedAtDesc();
    List<Testimonial> findAllByOrderByCreatedAtDesc();
}