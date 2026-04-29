package com.example.demo.repositories;

import com.example.demo.entities.ItineraryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ItineraryRepository extends JpaRepository<ItineraryEntity, Long>, JpaSpecificationExecutor<ItineraryEntity> {
    Page<ItineraryEntity> findByUserId(Long userId, Pageable pageable);
    Page<ItineraryEntity> findAllByActiveTrue(Pageable pageable);
    Page<ItineraryEntity> findAllByActiveFalse(Pageable pageable);
}
