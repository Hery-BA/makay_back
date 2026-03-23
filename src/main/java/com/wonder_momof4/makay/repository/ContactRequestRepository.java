package com.wonder_momof4.makay.repository;

import com.wonder_momof4.makay.model.ContactRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

 @Repository
 public interface ContactRequestRepository extends JpaRepository<ContactRequest, UUID> {
    List<ContactRequest> findAllByOrderByCreatedAtDesc();
    List<ContactRequest> findByIsReadFalseOrderByCreatedAtDesc();
    long countByIsReadFalse();
}

